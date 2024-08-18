package com.hunglp.interviewdemo.service;

import com.hunglp.interviewdemo.repository.UserRepository;
import com.hunglp.interviewdemo.entity.User;
import com.hunglp.interviewdemo.util.BatchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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


    public List<User> saveUsers(MultipartFile []files) throws Exception{
        List<CompletableFuture<List<User>>> futures = new ArrayList<>();
        // Xử lý từng file trong một luồng riêng biệt sử dụng thread pool executor
        for (MultipartFile file : files) {
            futures.add(excelService.processFile(file));
        }
        // Chờ tất cả các luồng hoàn thành
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // Thu thập kết quả
        List<User> allUsers = new ArrayList<>();
        for (CompletableFuture<List<User>> future : futures) {
            allUsers.addAll(future.get());
        }

//        // Lưu vào DB theo batch
        allUsers = processAndSaveUsers(allUsers, 1000);
        logger.info("Save done! size : {}", allUsers.size() );
        return allUsers;
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<List<User>> saveUsersBatch(List<User> users) {
        try {
            System.out.println("Saving batch in thread: " + Thread.currentThread().getName());
            List<User> savedUsers = userRepository.saveAll(users);
            return CompletableFuture.completedFuture(savedUsers);
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.failedFuture(e);
        }
    }

    public List<User> processAndSaveUsers(List<User> users, int batchSize) throws Exception {
        // Chia nhỏ danh sách user thành các batch
        List<List<User>> batches = BatchUtils.partitionList(users, batchSize);

        List<CompletableFuture<List<User>>> saveFutures = new ArrayList<>();
        for (List<User> batch : batches) {
            saveFutures.add(saveUsersBatch(batch));
        }

        // Chờ tất cả các batch hoàn thành
        CompletableFuture.allOf(saveFutures.toArray(new CompletableFuture[0])).join();

        // Thu thập tất cả các user đã lưu thành công
        List<User> allSavedUsers = new ArrayList<>();
        for (CompletableFuture<List<User>> future : saveFutures) {
            allSavedUsers.addAll(future.get()); // Lấy danh sách user từ mỗi batch
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
