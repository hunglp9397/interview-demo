package com.hunglp.interviewdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class MyResponse {
    private String message;
    private int statusCode;
    private Map<String, Object> data;
}
