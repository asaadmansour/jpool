package com.hale.jpool.core;

import com.hale.jpool.stats.PoolStats;
import com.hale.tracking.PoolMaintenance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionPool {

    private final DatabaseConfig config;
    private final ConnectionFactory connFactory;
    private final PoolConfig poolConfig;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition connectionAvailable = lock.newCondition();

    private final AtomicInteger totalCount = new AtomicInteger(0);
    private final ArrayDeque<PooledConnection> idleConnections = new ArrayDeque<>();

    private final PoolStats poolStats = PoolStats.getInstance();

    private static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);

    public ConnectionPool(DatabaseConfig config,
                          ConnectionFactory connFactory,
                          PoolConfig poolConfig) {
        this.config = config;
        this.connFactory = connFactory;
        this.poolConfig = poolConfig;

        while (idleConnections.size() < poolConfig.getMinConnectionIdle()) {
            try {
                Connection raw = connFactory.createConnection();
                PooledConnection pooled = new PooledConnection(raw);

                idleConnections.add(pooled);
                totalCount.incrementAndGet();

                logger.info("Connection created, total: {}", totalCount.get());
            } catch (SQLException e) {
                logger.error("Failed to create connection: {}", e.getMessage());
                throw new RuntimeException("Pool initialization failed", e);
            }
        }
    }
    @PoolMaintenance(developer = "Asaad",priority = PoolMaintenance.Priority.HIGH,status = PoolMaintenance.Status.STABLE)
    public PooledConnection borrow() throws InterruptedException, SQLException {
        lock.lock();
        try {
            while (idleConnections.isEmpty() &&
                    totalCount.get() >= poolConfig.getMaxConnectionSize()) {

                logger.warn("Pool exhausted, waiting... idle: {}, total: {}",
                        idleConnections.size(), totalCount.get());

                poolStats.incrementWaitingThreads();
                try {
                    connectionAvailable.await();
                } finally {
                    poolStats.decrementWaitingThreads();
                }
            }

            PooledConnection pooledConnection;

            if (!idleConnections.isEmpty()) {
                pooledConnection = idleConnections.poll();
                logger.info("Connection borrowed from pool, idle: {}, total: {}",
                        idleConnections.size(), totalCount.get());
            } else {
                Connection connection = connFactory.createConnection();
                pooledConnection = new PooledConnection(connection);

                totalCount.incrementAndGet();

                logger.info("New connection created, idle: {}, total: {}",
                        idleConnections.size(), totalCount.get());
            }

            pooledConnection.markBorrowed();

            poolStats.incrementTotalBorrowed();
            poolStats.incrementActiveBorrowed();

            return pooledConnection;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        } finally {
            lock.unlock();
        }
    }

    public void release(PooledConnection pooledConnection) {
        lock.lock();
        try {
            pooledConnection.markReturned();
            idleConnections.add(pooledConnection);

            connectionAvailable.signal();

            logger.info("Connection released, idle: {}", idleConnections.size());

            // stats (consistent)
            poolStats.decrementActiveBorrowed();

        } catch (Exception e) {
            logger.warn("Failed to release connection: {}", e.getMessage());
        } finally {
            lock.unlock();
        }
    }
    @PoolMaintenance(developer = "Asaad",priority = PoolMaintenance.Priority.MEDIUM,status = PoolMaintenance.Status.REFACTORING_STARTED)
    public void evictIdleConnections() {
        lock.lock();
        try {
            Iterator<PooledConnection> iterator = idleConnections.iterator();

            while (iterator.hasNext()) {
                PooledConnection pooled = iterator.next();

                long idleMillis = Duration
                        .between(pooled.getLastUsedAt(), Instant.now())
                        .toMillis();

                if (idleMillis > poolConfig.getIdleTimeoutMs() &&
                        idleConnections.size() > poolConfig.getMinConnectionIdle()) {

                    iterator.remove();
                    
                    totalCount.decrementAndGet();
                    poolStats.incrementTotalEvicted();

                    logger.info("Evicted idle connection, total: {}", totalCount.get());

                    try {
                        pooled.getRawConnection().close();
                    } catch (SQLException e) {
                        logger.warn("Failed to evict connection: {}", e.getMessage());
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void checkConnections() {
        lock.lock();
        try {
            Iterator<PooledConnection> iterator = idleConnections.iterator();

            while (iterator.hasNext()) {
                PooledConnection pooled = iterator.next();

                try {
                    if (!pooled.getRawConnection().isValid(2)) {
                        iterator.remove();

                        logger.warn("Dead connection removed, replacing...");

                        Connection conn = connFactory.createConnection();
                        idleConnections.add(new PooledConnection(conn));

                        logger.info("Replacement connection added, total: {}", totalCount.get());

                        poolStats.incrementTotalHealthReplaced();
                    }
                } catch (SQLException e) {
                    logger.warn("Health check failed: {}", e.getMessage());
                }
            }
        } finally {
            lock.unlock();
        }
    }
    @PoolMaintenance(developer = "Asaad",priority = PoolMaintenance.Priority.LOW,status = PoolMaintenance.Status.NEEDS_REVIEW)
    public void close() {
        lock.lock();
        try {
            for (PooledConnection pooled : idleConnections) {
                try {
                    pooled.getRawConnection().close();
                    logger.info("Connection closed");
                } catch (SQLException e) {
                    logger.warn("Failed to close connection: {}", e.getMessage());
                }
            }

            idleConnections.clear();
            totalCount.set(0);

        } finally {
            lock.unlock();
        }
    }

    public PoolStats getPoolStats() {
        return poolStats;
    }
}