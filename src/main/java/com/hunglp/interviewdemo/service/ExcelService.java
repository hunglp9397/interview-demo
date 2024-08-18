package com.hunglp.interviewdemo.service;

import com.hunglp.interviewdemo.entity.User;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ExcelService {

    @Async("taskExecutor")
    public CompletableFuture<List<User>> processFile(MultipartFile file) {
        log.info("Reading file: {}", file.getOriginalFilename());
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                log.info("Running thread: {} | {}", Thread.currentThread().getName(), line);
                String[] data = line.split(",");
                User user = new User(data[0], data[1], data[2]);
                users.add(user);
            }
        } catch (Exception e) {
            log.error("Failed to parse CSV file {}", e.getMessage(), e);
        }
        return CompletableFuture.completedFuture(users);
    }
}
