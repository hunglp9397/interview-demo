package com.hunglp.interviewdemo.controller;

import com.hunglp.interviewdemo.dto.BaseResponse;
import com.hunglp.interviewdemo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@RestController
@RequestMapping("/api/v1/test")
public class TestExceptionController {

    @Autowired
    private TestService testService;

    @PostMapping("")
    public BaseResponse test() {
        return testService.testUser(1L);
    }

    @PostMapping("/test-rest-api")
    public BaseResponse testRestAPI() {
        return testService.testRestAPI();
    }
}
