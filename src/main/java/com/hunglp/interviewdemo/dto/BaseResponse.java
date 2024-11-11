package com.hunglp.interviewdemo.dto;

import com.hunglp.interviewdemo.handler.Message;
import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

@Data
public class BaseResponse {
    T data;
    Message message;
}
