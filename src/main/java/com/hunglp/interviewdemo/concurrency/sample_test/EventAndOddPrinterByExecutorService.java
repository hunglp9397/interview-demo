package com.hunglp.interviewdemo.concurrency.sample_test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class EventAndOddPrinterByExecutorService {

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        IntStream.rangeClosed(1, 10).forEach(num -> {
            CompletableFuture<Integer> evenCompletableFuture = CompletableFuture.completedFuture(num).thenApplyAsync(x -> {
                if (x % 2 == 0) {
                    System.out.println(Thread.currentThread().getName() + " | countValue: " + x);
                }
                return num;
            }, executorService);

            CompletableFuture<Integer> oddCompletableFuture = CompletableFuture.completedFuture(num).thenApplyAsync(x -> {
                if (x % 2 != 0) {
                    System.out.println(Thread.currentThread().getName() + " | countValue: " + x);
                }
                return num;
            }, executorService);
            evenCompletableFuture.join();
            oddCompletableFuture.join();
        });
        executorService.shutdown();
    }
}
