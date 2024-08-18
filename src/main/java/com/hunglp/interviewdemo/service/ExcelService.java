package com.hunglp.interviewdemo.service;

import com.hunglp.interviewdemo.entity.User;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ExcelService {

    Logger logger = LoggerFactory.getLogger(ExcelService.class);

    @Async("taskExecutor")
    public CompletableFuture<List<User>> processFile(MultipartFile file) {

        List<User> users = new ArrayList<>();
        try {
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println("Running thread: " + Thread.currentThread().getName() + "|" + line);
                    final String[] data = line.split(",");
                    final User user = new User(data[0], data[1], data[2]);
                    users.add(user);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to parse CSV file {}", e);
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(users);


    }
}

