package com.hunglp.interviewdemo.concurrency.sample_test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Class for test different between execute vs submit
public class ExecuteVsSubmit {

    public static void main(String[] args) {

        Runnable taskSum1 = () -> {

            int sum = 0;
            for(int i = 0; i< 10; i++){
                sum+= i;
            }
            System.out.println("Task Sum done! Result : " + sum);
        };

        Callable<Integer> taskSum2 = ()  ->{
            int sum = 0;
            for(int i = 0; i< 10; i++){
                sum+= i;
            }
            return sum;
        };

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        executorService.execute(taskSum1);
        executorService.shutdown();

//        Integer result = executorService.submit(taskSum2).get();
//        System.out.println(result);
    }
}
