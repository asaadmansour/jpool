package com.hale.jpool.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionFactory {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionFactory.class);
    private final DatabaseConfig config;

    public ConnectionFactory(DatabaseConfig config) {
        this.config = config;
    }

    public Connection createConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(
                config.getUrl(),
                config.getUsername(),
                config.getPassword()
        );
        logger.debug("New connection created to {}", config.getUrl());
        return conn;
    }
}