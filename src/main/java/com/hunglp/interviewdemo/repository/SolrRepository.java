package com.hunglp.interviewdemo.repository;

import com.hunglp.interviewdemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// Giả lập solrRepository
public interface SolrRepository  extends JpaRepository<User,Integer> {
}
