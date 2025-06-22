package com.kwizera.deadlockDemo;

import java.util.concurrent.locks.ReentrantLock;

public class DeadlockDemonstrationApp {
    private static final ReentrantLock lockA = new ReentrantLock();
    private static final ReentrantLock lockB = new ReentrantLock();

    /*
    * both threads are likely to end up in a deadlock due to partial locking,
    * task1 acquires lockA and as it does that task2 acquires lockB.
    * next, task1 tries to acquire lockB but task2 already has it - at the same time task2 is trying to acquire lockA
    *  */
    public static void App() {
        Runnable task1 = () -> {
            try {
                lockA.lock();
                System.out.println("Task 1: Locked A");
                Thread.sleep(100);

                lockB.lock();
                System.out.println("Task 1: Locked B");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lockB.unlock();
                lockA.unlock();
            }
        };

        Runnable task2 = () -> {
            try {
                lockB.lock();
                System.out.println("Task 2: Locked B");
                Thread.sleep(100);

                lockA.lock();
                System.out.println("Task 2: Locked A");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lockA.unlock();
                lockB.unlock();
            }
        };

        new Thread(task1).start();
        new Thread(task2).start();
    }
}
