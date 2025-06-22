package com.kwizera.deadlockDemo;

import java.util.concurrent.locks.ReentrantLock;

public class LockInOrderDemonstrationApp {
    private static final ReentrantLock lockA = new ReentrantLock();
    private static final ReentrantLock lockB = new ReentrantLock();

    public static void App() {
        Runnable task1 = () -> {
            try {
                lockInOrder(lockA, lockB);
                System.out.println("Task 1 acquired both locks");

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Runnable task2 = () -> {
            try {
                lockInOrder(lockA, lockB);
                System.out.println("Task 2 acquired both locks");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        new Thread(task1, "Task1").start();
        new Thread(task2, "Task2").start();
    }

    public static void lockInOrder(ReentrantLock first, ReentrantLock second) throws InterruptedException {
        /* To avoid having both threads partially holding locks (which would resulted into a deadlock in the first demo),
        * we make a thread only resume work if it has acquired both locks.
        * In case of partial lock, a thread has to unlock that one lock to avoid a deadlock
        *  */
        while (true) {
            boolean gotFirst = first.tryLock();
            boolean gotSecond = second.tryLock();

            if (gotFirst && gotSecond) return;

            if (gotFirst) first.unlock();
            if (gotSecond) second.unlock();

            Thread.sleep(100);
        }
    }
}
