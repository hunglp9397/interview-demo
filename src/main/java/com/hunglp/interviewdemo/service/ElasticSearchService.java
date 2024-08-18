package com.hunglp.interviewdemo.service;

import com.hunglp.interviewdemo.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;


@Service
public class ElasticSearchService {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchService.class);

    @Async("taskExecutor")
    @CircuitBreaker(name = "elasticsearchService", fallbackMethod = "fallbackIndexUsers")
    public CompletableFuture<List<User>> indexUsers(List<User> users) {
        try {
            // Giả lập việc đánh index vào Elasticsearch
            System.out.println("Indexing batch in Elasticsearch with thread: " + Thread.currentThread().getName());

            // elasticsearchTemplate.save(users);
            logger.info("Indexed user, size:{}", users.size());
            return CompletableFuture.completedFuture(new ArrayList<>(users));
        } catch (Exception e) {
            logger.error("Error indexing users: {}", e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    // Fallback method in case Elasticsearch is not available
    public CompletableFuture<List<User>> fallbackIndexUsers(List<User> users, Throwable t) {
        logger.error("Elasticsearch service is down. Executing fallback logic. Error: {}", t.getMessage());
        // Xử lý fallback, ví dụ lưu log hoặc lưu dữ liệu vào một hệ thống khác.
        return CompletableFuture.completedFuture(new ArrayList<>());
    }
}