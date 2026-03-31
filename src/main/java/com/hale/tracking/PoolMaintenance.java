package com.hale.tracking;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PoolMaintenance {
    String developer();
    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }
    public enum Status {
        STABLE,
        REFACTORING_STARTED,
        NEEDS_REVIEW
    }
    Priority priority() default Priority.LOW;
    Status status() default  Status.STABLE;
}
