package com.hale;

import com.hale.demo.LoadSimulator;
import com.hale.jpool.core.*;
import com.hale.jpool.stats.PoolStats;
import com.hale.scheduler.IdleEvictionTask;
import com.hale.jpool.scheduler.HealthCheckTask;
import com.hale.stats.PoolStatsDTO;
import com.hale.stats.StatsServer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App {
    public static void main(String[] args) throws Exception {
        DatabaseConfig dbConfig = new DatabaseConfig("jpoolx", "postgres", "postgres");
        PoolConfig poolConfig = new PoolConfig();
        ConnectionFactory factory = new ConnectionFactory(dbConfig);
        ConnectionPool pool = new ConnectionPool(dbConfig, factory, poolConfig);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        scheduler.scheduleWithFixedDelay(new IdleEvictionTask(pool), 30, 30, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(new HealthCheckTask(pool), 60, 60, TimeUnit.SECONDS);
        StatsServer statsServer = new StatsServer(PoolStats.getInstance());
        statsServer.server();
        LoadSimulator simulator = new LoadSimulator(pool);
        simulator.simulate();
        scheduler.shutdown();
        pool.close();
    }
}