package com.hunglp.interviewdemo.handler.custom.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class BadGateWayErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        if(response.getStatusCode() == HttpStatus.BAD_GATEWAY){
            return true;
        }
        return false;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        System.out.println("Bad gateway");
    }
}
