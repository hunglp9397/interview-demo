package com.hunglp.interviewdemo.concurrency.sample_test;

import com.hunglp.interviewdemo.entity.Employee;
import com.hunglp.interviewdemo.repository.EmployeeRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CompletableFutureTest<I extends Number> {

    static int numThreads = Runtime.getRuntime().availableProcessors();

    // CASE 1 : Tính tổng các số từ 1 đến 100: Chia thành nhiều batch giao cho nhiều luồng xử lí
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

    // CASE 2: Combine 2 future
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

    // CASE 3 : Combine các future liên quan đến nhau
    public static void testCase3() throws ExecutionException, InterruptedException {

        // Combine dependent future :  Get rating of detail employee
        CompletableFuture<Integer> thenComposeResult =  getEmployeeDetail("79-021-3776").thenCompose(employee -> getRatings(employee));
        System.out.println("Ratings: "+ thenComposeResult.get());

        // Summary :
        // Dùng CompletableFuture : getEmployeeDetail() và getRatings() chạy ở hai luồng khác nhau
        // Dùng Stream:  toàn bộ thao tác sẽ chạy tuần tự trên luồng chính, điều này có thể gây ra "blocking".
    }


    // CASE 4 : Combine các future ko liên quan
    public static void testCase4() throws ExecutionException, InterruptedException {
        // Map Gender:Count
        CompletableFuture<Map<String,Long>> mapCompletableFuture = CompletableFuture.supplyAsync( () ->{
            System.out.println("Get Map<Gender:Count> " + Thread.currentThread());
            return EmployeeRepository.fetchEmployees().stream().collect(Collectors.groupingBy(Employee::getGender, Collectors.counting()));
        });


        // Get email
        CompletableFuture<List<String>> mailFuture = CompletableFuture.supplyAsync( () -> {
            System.out.println("Get email" + Thread.currentThread());
            return EmployeeRepository.fetchEmployees().stream().map(Employee::getEmail).collect(Collectors.toList());
        });

        //Combine 2 future
        CompletableFuture<String> combineResults = mapCompletableFuture.thenCombine(mailFuture, (empMap, emails) -> {
            System.out.println("Combine Map and Email Future ");
            return empMap + " " + emails;
        });

        System.out.println(combineResults.get());




    }

    public static CompletableFuture<Employee> getEmployeeDetail(String empId){
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("Get details" + Thread.currentThread());
           return  EmployeeRepository.fetchEmployees().stream().filter(emp -> empId.equals(emp.getEmployeeId())).findAny().orElse(null);
        });
    }

    public static CompletableFuture<Integer> getRatings(Employee employee){
        return CompletableFuture.supplyAsync( () -> {
            System.out.println("Get ratings" + Thread.currentThread());
            return employee.getRating();
        });
    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        testCase4();
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
