package com.hunglp.interviewdemo.service;

import com.hunglp.interviewdemo.entity.User;
import com.hunglp.interviewdemo.repository.SolrRepository;
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
public class SolrService {

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<List<User>> saveToSolr(List<User> users) {
        try {
            log.info("Saving batch to Solr with thread: {}", Thread.currentThread().getName());
            // List<User> savedUsers = solrRepository.saveAll(users);
            return CompletableFuture.completedFuture(users);
        } catch (Exception e) {
            log.error("Error saving to Solr: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
}