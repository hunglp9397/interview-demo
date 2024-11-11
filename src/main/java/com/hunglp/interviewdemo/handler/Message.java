package com.hunglp.interviewdemo.handler;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Message {
    private int statusCode;
    private Date timestamp;
    private String message;
    private String description;
}
