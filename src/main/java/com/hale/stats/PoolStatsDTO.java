package com.hale.stats;

import com.hale.jpool.stats.PoolStats;

public class PoolStatsDTO {
    private final long totalBorrowed;
    private final long activeBorrowed;
    private final long waitingThreads;
    private final long totalEvicted;
    private final long totalHealthReplaced;

    public PoolStatsDTO(PoolStats poolStats) {
        this.totalBorrowed = poolStats.getTotalBorrowed();
        this.activeBorrowed = poolStats.getActiveBorrowed();
        this.waitingThreads = poolStats.getWaitingThreads();
        this.totalEvicted = poolStats.getTotalEvicted();
        this.totalHealthReplaced = poolStats.getTotalHealthReplaced();
    }

    // Getters are optional for Gson, but good practice
    public long getTotalBorrowed()       { return totalBorrowed; }
    public long getActiveBorrowed()      { return activeBorrowed; }
    public long getWaitingThreads()      { return waitingThreads; }
    public long getTotalEvicted()        { return totalEvicted; }
    public long getTotalHealthReplaced() { return totalHealthReplaced; }
}