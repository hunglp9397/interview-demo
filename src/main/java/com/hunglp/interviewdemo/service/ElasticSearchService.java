package com.hunglp.interviewdemo.service;

import com.hunglp.interviewdemo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;


@Service
@Slf4j
public class ElasticSearchService {

    @Async("taskExecutor")
    @CircuitBreaker(name = "elasticsearchService", fallbackMethod = "fallbackIndexUsers")
    public CompletableFuture<List<User>> indexUsers(List<User> users) {
        try {
            log.info("Indexing batch in Elasticsearch with thread: {}", Thread.currentThread().getName());
            // elasticsearchTemplate.save(users);
            return CompletableFuture.completedFuture(new ArrayList<>(users));
        } catch (Exception e) {
            log.error("Error indexing users: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<List<User>> fallbackIndexUsers(List<User> users, Throwable t) {
        log.error("Elasticsearch service is down. Executing fallback logic. Error: {}", t.getMessage());
        return CompletableFuture.completedFuture(new ArrayList<>());
    }
}