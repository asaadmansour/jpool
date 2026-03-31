package com.hale.jpool.core;

public final class PoolConfig {
    private final int maxConnectionSize;
    private final int minConnectionIdle;
    private final int connectionTimeoutMs;
    private final int idleTimeoutMs;
    private final String validationQuery;

    public PoolConfig(int maxConnectionSize, int minConnectionIdle, int connectionTimeoutMs, int idleTimeoutMs, String validationQuery) {
        this.maxConnectionSize = maxConnectionSize;
        this.minConnectionIdle = minConnectionIdle;
        this.connectionTimeoutMs = connectionTimeoutMs;
        this.idleTimeoutMs = idleTimeoutMs;
        this.validationQuery = validationQuery;
    }

    // convenience constructor with sensible defaults
    public PoolConfig() {
        this(12, 4, 30000, 600000, "SELECT 1");
    }

    public int getMaxConnectionSize()  { return maxConnectionSize; }
    public int getMinConnectionIdle()  { return minConnectionIdle; }
    public int getConnectionTimeoutMs(){ return connectionTimeoutMs; }
    public int getIdleTimeoutMs()      { return idleTimeoutMs; }
    public String getValidationQuery() { return validationQuery; }
}