package com.hunglp.interviewdemo.service;

import com.hunglp.interviewdemo.dto.BaseResponse;
import com.hunglp.interviewdemo.handler.Message;
import com.hunglp.interviewdemo.handler.custom.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
public class TestService {

    public BaseResponse testUser(Long id){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(null);
        baseResponse.setMessage(new Message(HttpStatus.OK.value(), new Date(), "Success", ""));

        if(id == 1){
            throw new ResourceNotFoundException("Resource not found with ID: " + id);
        }
        return baseResponse;
    }

    public BaseResponse testRestAPI() {

        final String uri = "http://localhost:8080/springrestexample/employees.xml";

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        System.out.println(result);

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(null);
        baseResponse.setMessage(new Message(HttpStatus.OK.value(), new Date(), "Success", ""));
        return baseResponse;
    }
}
