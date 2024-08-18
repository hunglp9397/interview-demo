package com.hunglp.interviewdemo.service;

import com.hunglp.interviewdemo.dto.UserDto;
import com.hunglp.interviewdemo.repository.UserRepository;
import com.hunglp.interviewdemo.entity.User;
import com.hunglp.interviewdemo.util.BatchUtils;
import com.hunglp.interviewdemo.util.TaskUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private BatchUserService batchUserService;

    @Autowired
    private SolrService solrService;
    
    @Autowired
    private TaskUtils taskUtils;

    public List<User> saveUsers(MultipartFile[] files) throws Exception {
        List<User> allUsers = processFiles(files);
        allUsers = processAndSaveUsers(allUsers, 1000);
        log.info("Saved user, size: {}", allUsers.size());
        return allUsers;
    }

    private List<User> processFiles(MultipartFile[] files) {
        List<CompletableFuture<List<User>>> futures = Arrays.stream(files)
                .map(file -> excelService.processFile(file)
                        .exceptionally(ex -> taskUtils.handleException(ex, "processFile " + file.getOriginalFilename())))
                .collect(Collectors.toList());

        return taskUtils.collectResults(futures);
    }

    private List<User> processAndSaveUsers(List<User> users, int batchSize) throws Exception {
        List<List<User>> batches = BatchUtils.partitionList(users, batchSize);

        List<CompletableFuture<List<User>>> saveFutures = batches.stream()
                .map(batch -> batchUserService.saveUsersBatch(batch)
                        .exceptionally(ex -> taskUtils.handleException(ex, "saveUsersBatch")))
                .collect(Collectors.toList());

        return taskUtils.collectResults(saveFutures);
    }


    public List<UserDto> syncToSolrAndElasticsearch() {
        List<User> users = userRepository.findAll();
        List<List<User>> batches = BatchUtils.partitionList(users, 500);
        log.info("Total: {} | Batch: 500", users.size());

        List<CompletableFuture<List<User>>> solrFutures = new ArrayList<>();
        List<CompletableFuture<List<User>>> elasticFutures = new ArrayList<>();

        for (List<User> batch : batches) {
            CompletableFuture<List<User>> solrFuture = solrService.saveToSolr(batch);
            solrFutures.add(solrFuture);

            CompletableFuture<List<User>> elasticFuture = solrFuture.thenCompose(savedUsers ->
                    elasticSearchService.indexUsers(savedUsers));
            elasticFutures.add(elasticFuture);
        }

        // Chờ tất cả các batch hoàn thành cho Solr và Elasticsearch
        CompletableFuture.allOf(solrFutures.toArray(new CompletableFuture[0])).join();
        CompletableFuture.allOf(elasticFutures.toArray(new CompletableFuture[0])).join();

        // Thu thập kết quả từ Solr và Elasticsearch
        List<User> indexedUsersInSolr = taskUtils.collectResults(solrFutures);
        log.info("Indexed Solr! size: {}", indexedUsersInSolr.size());

        List<User> indexedUsersInElasticsearch = taskUtils.collectResults(elasticFutures);
        log.info("Indexed Elasticsearch! size: {}", indexedUsersInElasticsearch.size());

        // Chuyển đổi từ User sang UserDto
        return indexedUsersInSolr.stream()
                .map(user -> {
                    UserDto userDto = new UserDto(user);
                    userDto.setIndexed(1);
                    return userDto;
                })
                .collect(Collectors.toList());
    }
}
