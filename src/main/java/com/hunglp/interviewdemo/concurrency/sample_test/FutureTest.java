package com.hunglp.interviewdemo.concurrency.sample_test;


// Executor submit có thể trả về kết quả của task nhưng Dùng future sẽ hỗ trợ việc kiểm tra task đã chạy xong hay chưa
// Bằng các phương thức future.cancel và future.isDone()

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

public class FutureTest {

    public static int calculateSumFromRange(int startRange, int endRange){
        int sum = 0;
        for(int i = startRange; i < endRange; i++){
            System.out.println(Thread.currentThread() + " index : " + i);
            sum += i;
        }
        return sum;
    }

    public static void main(String[] args) {
        // Lấy số thread có sẵn trong hệ thống
        int numThreads = Runtime.getRuntime().availableProcessors();
        System.out.println("Total threads: " + numThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        long start = System.currentTimeMillis();

        try {

            int sizeData = 100;
            // Chia công việc thành các phần nhỏ và gửi vào các thread
            int range = 100 / numThreads; // Tính số phần công việc mỗi thread xử lý
            List<Future<Integer>> futures = new ArrayList<>();

            // Chia công việc cho các thread
            IntStream.range(0, numThreads).forEach(i -> {
                int startRange = i * range;
                int endRange = (i == numThreads - 1) ? sizeData : (i + 1) * range;  // Phần cuối cùng xử lý hết phần còn lại
                futures.add(executorService.submit(() -> calculateSumFromRange(startRange, endRange)));
            });

            // Tính tổng kết quả từ các thread
            int totalSum = 0;
            for (Future<Integer> future : futures) {
                totalSum += future.get(); // Lấy kết quả của mỗi task
            }

            System.out.println("Sum: " + totalSum);
        } catch (Exception e) {
            System.out.println("Error while running task!" +  e.getMessage());
            e.printStackTrace();
        } finally {
            executorService.shutdown(); // Đảm bảo thread pool được shutdown sau khi xong
            long end = System.currentTimeMillis();
            System.out.println("Total time execute: " + (end - start));
        }
    }
}
