package com.hale.jpool.core;

public final class DatabaseConfig {
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    public DatabaseConfig(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    // convenience constructor with default host and port
    public DatabaseConfig(String database, String username, String password) {
        this("localhost", 5432, database, username, password);
    }

    public String getHost()     { return host; }
    public int getPort()        { return port; }
    public String getDatabase() { return database; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public String getUrl() {
        return "jdbc:postgresql://" + host + ":" + port + "/" + database;
    }
}