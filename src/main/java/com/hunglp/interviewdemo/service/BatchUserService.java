package com.hunglp.interviewdemo.service;


import com.hunglp.interviewdemo.entity.User;
import com.hunglp.interviewdemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class BatchUserService {
    @Autowired
    private UserRepository userRepository;

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
}
