package com.hale.jpool.core;
import java.sql.Connection;
import java.time.Instant;

public class PooledConnection {
    private final Connection rawConnection;
    private final Instant createdAt;
    private Instant lastUsedAt;
    private boolean inUse;
    public PooledConnection(Connection rawConnection) {
        this.rawConnection = rawConnection;
        this.createdAt = Instant.now();
        this.lastUsedAt = Instant.now();
        this.inUse = false;
    }
    public Connection getRawConnection() { return rawConnection; }
    public Instant getCreatedAt()        { return createdAt; }
    public Instant getLastUsedAt()       { return lastUsedAt; }
    public boolean isInUse()             { return inUse; }
    public void markBorrowed() {
        this.inUse = true;
        this.lastUsedAt = Instant.now();
    }
    public void markReturned() {
        this.inUse = false;
        this.lastUsedAt = Instant.now();
    }
}
