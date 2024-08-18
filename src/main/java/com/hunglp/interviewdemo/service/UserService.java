package com.hunglp.interviewdemo.service;

import com.hunglp.interviewdemo.repository.UserRepository;
import com.hunglp.interviewdemo.entity.User;
import com.hunglp.interviewdemo.util.BatchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class UserService {

    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private BatchUserService batchUserService;


    public List<User> saveUsers(MultipartFile []files) throws Exception{
        List<CompletableFuture<List<User>>> futures = new ArrayList<>();

        // Đọc file excel
        for (MultipartFile file : files) {
            CompletableFuture<List<User>> future = excelService.processFile(file)
                    .exceptionally(ex -> {
                        logger.error("Error processing file: {}", file.getOriginalFilename(), ex);
                        return Collections.emptyList();
                    });
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        List<User> allUsers = futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(Collectors.toList());

//      // Lưu vào DB theo batch
        allUsers = processAndSaveUsers(allUsers, 1000);

        // index vào Elasticsearch
       elasticSearchService.indexUsers(allUsers);

        logger.info("Saved user, size:{}", allUsers.size() );
        return allUsers;
    }



    public List<User> processAndSaveUsers(List<User> users, int batchSize) throws Exception {
        List<List<User>> batches = BatchUtils.partitionList(users, batchSize);

        List<CompletableFuture<List<User>>> saveFutures = new ArrayList<>();
        for (List<User> batch : batches) {
            saveFutures.add(batchUserService.saveUsersBatch(batch));
        }

        CompletableFuture.allOf(saveFutures.toArray(new CompletableFuture[0])).join();

        List<User> allSavedUsers = new ArrayList<>();
        for (CompletableFuture<List<User>> future : saveFutures) {
            allSavedUsers.addAll(future.get());
        }

        return allSavedUsers;
    }


    public List<User> findAllUsers(){
        logger.info("get list of user by "+Thread.currentThread().getName());
        long start = System.currentTimeMillis();
        List<User> users= userRepository.findAll();
        long end = System.currentTimeMillis();
        logger.info("Total time {}", (end - start));
        return users;
    }
}