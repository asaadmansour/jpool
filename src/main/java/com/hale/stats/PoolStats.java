package com.hale.jpool.stats;

import java.util.concurrent.atomic.AtomicLong;

public class PoolStats {
    private static final PoolStats INSTANCE = new PoolStats();

    private PoolStats() {}

    public static PoolStats getInstance() {
        return INSTANCE;
    }

    private final AtomicLong totalBorrowed     = new AtomicLong(0);
    private final AtomicLong activeBorrowed    = new AtomicLong(0);
    private final AtomicLong waitingThreads    = new AtomicLong(0);
    private final AtomicLong totalEvicted      = new AtomicLong(0);
    private final AtomicLong totalHealthReplaced = new AtomicLong(0);

    public void incrementTotalBorrowed()      { totalBorrowed.incrementAndGet(); }
    public void incrementActiveBorrowed()     { activeBorrowed.incrementAndGet(); }
    public void decrementActiveBorrowed()     { activeBorrowed.decrementAndGet(); }
    public void incrementWaitingThreads()     { waitingThreads.incrementAndGet(); }
    public void decrementWaitingThreads()     { waitingThreads.decrementAndGet(); }
    public void incrementTotalEvicted()       { totalEvicted.incrementAndGet(); }
    public void incrementTotalHealthReplaced(){ totalHealthReplaced.incrementAndGet(); }

    public long getTotalBorrowed()       { return totalBorrowed.get(); }
    public long getActiveBorrowed()      { return activeBorrowed.get(); }
    public long getWaitingThreads()      { return waitingThreads.get(); }
    public long getTotalEvicted()        { return totalEvicted.get(); }
    public long getTotalHealthReplaced() { return totalHealthReplaced.get(); }
}