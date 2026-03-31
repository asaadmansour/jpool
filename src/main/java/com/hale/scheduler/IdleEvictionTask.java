package com.hale.scheduler;

import com.hale.jpool.core.ConnectionPool;
import com.hale.jpool.core.PooledConnection;

public class IdleEvictionTask extends Thread{
    private final ConnectionPool connectionPool;
    public IdleEvictionTask(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }
    @Override
    public void run() {
        connectionPool.evictIdleConnections();
    }
}
