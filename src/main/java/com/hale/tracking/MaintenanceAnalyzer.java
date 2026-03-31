package com.hale.tracking;

import java.lang.reflect.Method;

public class MaintenanceAnalyzer {
    public static void getMaintenanceReport(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(PoolMaintenance.class)) {
                PoolMaintenance annotation = method.getAnnotation(PoolMaintenance.class);

                System.out.println("Method: " + method.getName());
                System.out.println("Developer: " + annotation.developer());
                System.out.println("Priority: " + annotation.priority());
                System.out.println("Status: " + annotation.status());
                System.out.println("-----");
            }
        }
    }
}
