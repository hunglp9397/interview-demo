package com.hunglp.interviewdemo.concurrency.sample_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hunglp.interviewdemo.entity.Employee;
import com.hunglp.interviewdemo.repository.EmployeeRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CompletableFutureTest {

    static int numThreads = Runtime.getRuntime().availableProcessors();

    // CASE 1
    public static void testCase1() {
        long start = System.currentTimeMillis();
        // Lấy số thread có sẵn trong hệ thống

        System.out.println("Total threads: " + numThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        int sizeData = 100;
        int range = sizeData / numThreads; // Chia dữ liệu cho từng thread

        // Tính tổng cho mỗi phần trong một CompletableFuture
        CompletableFuture<Integer>[] tasks = IntStream.range(0, numThreads)
                .mapToObj(i -> {
                    int startRange = i * range;
                    int endRange = (i == numThreads - 1) ? sizeData : (i + 1) * range; // Phần cuối xử lý hết dữ liệu còn lại
                    return CompletableFuture.supplyAsync(() -> calculateSumFromRange(startRange, endRange), executorService);
                })
                .toArray(CompletableFuture[]::new);

        // Kết hợp tất cả các CompletableFuture và nhân tổng kết quả với 2
        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(tasks)
                .thenApplyAsync(v -> IntStream.range(0, tasks.length)
                        .mapToObj(i -> tasks[i])
                        .mapToInt(task -> task.join()) // Join để lấy kết quả từ từng CompletableFuture
                        .sum()) // Tổng tất cả các kết quả
                .thenApply(sum -> sum * 2) // Nhân tổng với 2
                .thenAccept(result -> System.out.println("Final Result: " + result)); // In kết quả cuối cùng

        // Đợi tất cả các tác vụ hoàn thành
        combinedFuture.join();
        executorService.shutdown(); // Đảm bảo thread pool được shutdown
        long end = System.currentTimeMillis();

        System.out.println("Total time execute: " + (end - start));
    }


    // CASE 2
    public static void testCase2() {
        long start = System.currentTimeMillis();
        System.out.println("Total thread: " + numThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        try {
            CompletableFuture<List<Employee>> runAsyncFuture = CompletableFuture
                    .supplyAsync(() -> {
                        System.out.println(Thread.currentThread());
                        System.out.println("Fetching database ...");
                        return EmployeeRepository.fetchEmployees();
                    }, executorService)
                    .thenApplyAsync(employees -> {
                        System.out.println( Thread.currentThread());
                        System.out.println("Filter new Joiner Employee ..." + Thread.currentThread().getName());
                        return employees.stream().filter(e -> "FALSE".equals(e.getNewJoiner())).collect(Collectors.toList());
                    }, executorService);
            System.out.println("Size employees: " + runAsyncFuture.get().size());
        } catch (Exception e) {
            System.out.println("Error while running testCase2" + e.getMessage());
        } finally {

            if (executorService != null) {
                executorService.shutdown();  // Đảm bảo đóng ExecutorService
            }
            long end = System.currentTimeMillis();
            System.out.println("Time execute : " + (end - start));
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        testCase2();
    }


    public static int calculateSumFromRange(int startRange, int endRange) {
        int sum = 0;
        for (int i = startRange; i < endRange; i++) {
            System.out.println(Thread.currentThread() + " index : " + i);
            sum += i;
        }
        return sum;
    }
}
