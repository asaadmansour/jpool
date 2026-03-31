Week 1 — Core pool
Days 1–2: Setup + PoolConfig + ConnectionFactory

Maven project, Java 21, MySQL JDBC driver dependency
PoolConfig: maxSize, minIdle, connectionTimeout, idleTimeout, validationQuery
ConnectionFactory: wraps DriverManager.getConnection(), throws typed exception on failure

Days 3–4: ConnectionPool — the heart

ArrayDeque<PooledConnection> as the idle pool
ReentrantLock + Condition notEmpty to coordinate borrow/return
Semaphore(maxSize) to hard-cap concurrent connections
borrow(timeout) → CompletableFuture<PooledConnection> using virtual thread
release(conn) → puts back, signals notEmpty
AtomicInteger counters: active, idle, waiting, totalBorrowed

Day 5: PooledConnection wrapper

Wraps real java.sql.Connection
Tracks lastUsedAt, createdAt, borrowCount
Override close() to return to pool instead of actually closing


Week 2 — Scheduler, Stats, Polish
Days 6–7: Background scheduler

ScheduledThreadPoolExecutor(2) for eviction + health checks
IdleEvictionTask: every 30s, lock pool, remove connections idle > idleTimeout, replace with fresh ones down to minIdle
HealthCheckTask: every 60s, for each idle connection run validationQuery (e.g. SELECT 1), remove if dead — use virtual threads here so checks don't block the scheduler thread

Day 8: StatsServer — GET /stats

com.sun.net.httpserver.HttpServer (built-in Java, no deps needed)
StatsServer reads stats using StampedLock optimistic read
Returns JSON: { "active": 5, "idle": 3, "waiting": 2, "totalBorrowed": 1042, "rejections": 1 }

Day 9: LoadSimulator + edge cases

Spin up 50 virtual threads, each borrows a connection, sleeps 50–200ms (simulating a query), releases
Test: pool exhaustion → RejectedExecutionException after timeout
Test: connection leak detection (borrow and never release)
Test: dead connection recovery (kill MySQL process, watch health check clean up)

Day 10: Write-up + README

Architecture diagram, design decisions, benchmark numbers (throughput vs HikariCP)

extra

Step 1: Create the Tracker Annotation
Create an annotation named @PoolMaintenance inside a new package com.hale.tracking.

This annotation must only be applicable to Methods (@Target).
This annotation must be available to read at Runtime (@Retention).
The annotation requires the following elements:

developer — A String type with no default value.
priority — An Enum type named Priority containing LOW, MEDIUM, and HIGH. It must be declared inside the annotation. The default value should be LOW.
status — An Enum type named Status containing STABLE, REFACTORING_STARTED, and NEEDS_REVIEW. It must be declared inside the annotation. The default value should be STABLE.
Step 2: Apply Your Annotation
Open 

ConnectionPool.java
 and apply your new @PoolMaintenance annotation to three methods:


borrow()
 (Give it HIGH priority, and STABLE status).

evictIdleConnections()
 (Give it MEDIUM priority, and REFACTORING_STARTED status).

close()
 (Give it LOW priority, and NEEDS_REVIEW status). Make sure you provide your name for the developer element on all of them!
Step 3: Implement the Analyzer
Create a class called MaintenanceAnalyzer in the com.hale.tracking package.

Create a method inside it: public static String getMaintenanceReport(Class<?> clazz)
The method must accept a Class object to analyze (e.g., ConnectionPool.class).
You must use the Reflection API (getDeclaredMethods()) to loop through all methods in the provided class.
Check if each method has the @PoolMaintenance annotation present using Reflection.