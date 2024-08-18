package com.hunglp.interviewdemo.service;


import com.hunglp.interviewdemo.entity.User;
import com.hunglp.interviewdemo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class BatchUserService {

    @Autowired
    private UserRepository userRepository;

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<List<User>> saveUsersBatch(List<User> users) {
        try {
            log.info("Saving batch in thread: {}", Thread.currentThread().getName());
            List<User> savedUsers = userRepository.saveAll(users);
            return CompletableFuture.completedFuture(savedUsers);
        } catch (Exception e) {
            log.error("Error saving batch: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
}