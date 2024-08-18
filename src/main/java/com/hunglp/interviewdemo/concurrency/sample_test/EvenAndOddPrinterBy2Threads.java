package com.hunglp.interviewdemo.concurrency.sample_test;

// Multithread in lần lượt 2 số chẵn lẻ sử dụng 2 Thread-Runable
public class EvenAndOddPrinterBy2Threads implements Runnable {

    static int count = 1;

    Object object;


    public EvenAndOddPrinterBy2Threads(Object object) {
        this.object = object;
    }

    @Override
    public void run() {
        /*
         Dùng object wait và notify ở đây có tác dụng như sau:
            - Khi [THREAD EVEN] chạy xong thì sẽ chờ cho [THREAD ODD] chạy
            - Khi [THREAD ODD] chạy xong thì sẽ thông báo cho [THREAD EVEN] là đã chạy xong, [THREAD EVEN] tiếp tục start
            ...
          Dùng synchronized ở đây là object Lock để đảm bảo 2 thread này chạy lần lượt
        */

        while (count <= 10) {
            if (count % 2 == 0 && Thread.currentThread().getName().equals("[THREAD EVEN]")) {
                synchronized (object) {
                    System.out.println(Thread.currentThread().getName() + " | countValue : " + count);
                    count++;
                    try {
                        object.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            if (count % 2 != 0 && Thread.currentThread().getName().equals("[THREAD ODD]")) {
                synchronized (object) {
                    System.out.println(Thread.currentThread().getName() + " | countValue : " + count);
                    count++;
                    object.notify();
                }
            }
        }
    }

    public static void main(String[] args) {

        Object objLock = new Object();
        Runnable r1 = new EvenAndOddPrinterBy2Threads(objLock);
        Runnable r2 = new EvenAndOddPrinterBy2Threads(objLock);

        Thread t1 = new Thread(r1, "[THREAD EVEN]");
        Thread t2 = new Thread(r2, "[THREAD ODD]");

        t1.start();
        t2.start();


    }


}
