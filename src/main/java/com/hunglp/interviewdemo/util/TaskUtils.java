package com.hunglp.interviewdemo.util;

import com.hunglp.interviewdemo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskUtils {

    public List<User> handleException(Throwable ex, String context) {
        // Log chi tiết lỗi
        log.error("Error occurred during {}: {}", context, ex.getMessage(), ex);
        // Trả về một danh sách rỗng để tiếp tục quá trình mà không gây lỗi
        return Collections.emptyList();
    }

    public <T> List<T> collectResults(List<CompletableFuture<List<T>>> futures) {
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
