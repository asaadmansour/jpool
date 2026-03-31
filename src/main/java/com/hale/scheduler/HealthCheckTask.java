package com.hale.jpool.scheduler;

import com.hale.jpool.core.ConnectionPool;

public class HealthCheckTask implements Runnable {
    private final ConnectionPool pool;

    public HealthCheckTask(ConnectionPool pool) {
        this.pool = pool;
    }

    @Override
    public void run() {
        pool.checkConnections();
    }
}