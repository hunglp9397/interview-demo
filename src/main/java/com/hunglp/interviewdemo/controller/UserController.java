package com.hunglp.interviewdemo.controller;

import com.hunglp.interviewdemo.dto.MyResponse;
import com.hunglp.interviewdemo.dto.UserDto;
import com.hunglp.interviewdemo.entity.User;
import com.hunglp.interviewdemo.service.ExcelService;
import com.hunglp.interviewdemo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping(value = "/users", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = "application/json")
    public ResponseEntity<MyResponse> saveUsers(@RequestParam(value = "files") MultipartFile[] files) throws Exception {
        List<User> users = userService.saveUsers(files);

        Map<String, Object> data = new HashMap<>();
        data.put("users", users);
        MyResponse response = new MyResponse(HttpStatus.CREATED.toString(), HttpStatus.CREATED.value(), data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/users/sync-to-solr-and-es")
    public ResponseEntity<MyResponse> syncToSolrAndElasticsearch() {
        List<UserDto> userDtos = userService.syncToSolrAndElasticsearch();

        Map<String, Object> data = new HashMap<>();
        data.put("users", userDtos);
        MyResponse response = new MyResponse(HttpStatus.OK.toString(), HttpStatus.OK.value(), data );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}