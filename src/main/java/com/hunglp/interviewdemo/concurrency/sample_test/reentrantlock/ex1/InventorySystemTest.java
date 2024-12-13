package com.hunglp.interviewdemo.concurrency.sample_test.reentrantlock.ex1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class InventorySystemTest {
    private final ReentrantLock lock = new ReentrantLock();


    private int itemInStock = 100;

    public void decrementStock(){
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "| Inventory : " + itemInStock);
            if (itemInStock > 0) {
                itemInStock--;
            } else {
                System.out.println("Out of stock!");
            }
        } catch (Exception e) {
            System.out.println("Error while decrementing item!" + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        InventorySystemTest inventorySystemTest = new InventorySystemTest();
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // Giả sử có 2 thao tác của người dùng: Bấm chọn mua nhưng chưa thực hiện thanh toán
        for(int i = 0; i < 20; i++){
            executorService.submit( () -> inventorySystemTest.decrementStock());
        }
        executorService.shutdown();
    }
}
