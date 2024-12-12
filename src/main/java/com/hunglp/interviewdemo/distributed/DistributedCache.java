package com.hunglp.interviewdemo.distributed;

import lombok.SneakyThrows;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.params.SetParams;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

class DistributedCache {

    static String USER_ID = "A";

    static Map<String, Integer> USERS = new HashMap<>() {{
        put(USER_ID, 1000);
    }};

    static JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost", 6379,"default", "eYVX7EwVmmxKPCDmwMtyKVge8oLd2t81");

    public static void main(String[] args) {
        //
        CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> lock(DistributedCache::fundOut)),
                CompletableFuture.runAsync(() -> lock(DistributedCache::fundTransfer))
        ).join();

        // {A=500}
        System.out.println(USERS);
    }

    static void fundOut() {
        Integer balance = getBalance(USER_ID);
        balance = balance - 200;
        updateBalance(USER_ID, balance);
    }

    static void fundTransfer() {
        Integer balance = getBalance(USER_ID);
        balance = balance - 300;
        updateBalance(USER_ID, balance);
    }

    @SneakyThrows
    static Integer getBalance(String userId) {
        Integer balance = USERS.get(userId);
        Thread.sleep(1000L);
        return balance;
    }

    static void updateBalance(String userId, Integer balance) {
        USERS.put(userId, balance);
    }

    static void lock(Runnable runnable) {
        try {
            if (!tryLock(USER_ID, 3000, 4000)) {
                return;
            }

            //
            runnable.run();
        } finally {
            unlock(USER_ID);
        }
    }

    static boolean tryLock(String lockKey, int lockExpire, int tryLockTimeOut) {
        try (Jedis jedis = jedisPool.getResource()) {
            //
            long start = System.currentTimeMillis();

            //
            while (true) {
                //
                String lockRes = jedis.set(lockKey, "1", new SetParams().nx().px(lockExpire));

                //
                if (lockRes != null) {
                    return true;
                }

                //
                if (System.currentTimeMillis() - start > tryLockTimeOut) {
                    throw new RuntimeException("Try lock timeout");
                }
            }
        }
    }

    static void unlock(String lockKey) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(lockKey);
        }
    }
}
