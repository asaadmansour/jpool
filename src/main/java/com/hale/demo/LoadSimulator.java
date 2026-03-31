package com.hale.demo;

import com.hale.jpool.core.ConnectionPool;

import java.util.concurrent.*;

public class LoadSimulator {
    private final ConnectionPool pool;
    public LoadSimulator(ConnectionPool pool) {
        this.pool = pool;
    }
    public void simulate() throws InterruptedException {
    ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        for (int i = 0; i < 50; i++) {
            executor.submit(() -> {
                long start = System.nanoTime();
                try {
                    var conn = pool.borrow();
                    long borrowTime = System.nanoTime() - start;
                    System.out.printf("Borrowed in %.3f ms%n", borrowTime / 1_000_000.0);
                    Thread.sleep(ThreadLocalRandom.current().nextInt(50, 200));

                    pool.release(conn);
                } catch (Exception e) {
                    System.out.println("Failed: " + e.getMessage());
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);
    }
}
