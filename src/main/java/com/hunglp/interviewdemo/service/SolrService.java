package com.hunglp.interviewdemo.service;

import com.hunglp.interviewdemo.entity.User;
import com.hunglp.interviewdemo.repository.SolrRepository;
import com.hunglp.interviewdemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class SolrService {

    @Autowired
    private SolrRepository solrRepository;

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<List<User>> saveToSolr(List<User> users) {
        try {
            System.out.println("Saving batch to Solr with thread: " + Thread.currentThread().getName());
//            List<User> savedUsers = solrRepository.saveAll(users);
            return CompletableFuture.completedFuture(users);
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.failedFuture(e);
        }
    }
}
