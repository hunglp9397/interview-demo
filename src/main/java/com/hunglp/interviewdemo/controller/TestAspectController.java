package com.hunglp.interviewdemo.controller;


import com.hunglp.interviewdemo.annotations.AuthorizedFor;
import com.hunglp.interviewdemo.annotations.CustomPermission;
import com.hunglp.interviewdemo.annotations.LogExecutionTime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test-aspect")
public class TestAspectController {

    @LogExecutionTime
    @GetMapping("/log")
    public String testLog(@RequestParam(value = "i", required = false, defaultValue = "10") int i) {
        return "Test log OK";
    }

    @AuthorizedFor(customPermission = CustomPermission.READ)
    @GetMapping("/auth")
    public String testAuth() {
        return "Auth OK";
    }
}
