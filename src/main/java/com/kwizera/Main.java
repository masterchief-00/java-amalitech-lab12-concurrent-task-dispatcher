package com.kwizera;

import com.kwizera.enums.TaskStatus;
import com.kwizera.models.Task;
import com.kwizera.threads.Consumer;
import com.kwizera.threads.Producer;
import com.kwizera.threads.Watchdog;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static final BlockingQueue<Task> queue = new PriorityBlockingQueue<>();
    private static final ConcurrentHashMap<UUID, TaskStatus> tasksState = new ConcurrentHashMap<>();
    private static final Map<Thread, Long> activityTracker = new HashMap<>();
    private static final AtomicInteger tasksCounter = new AtomicInteger(0);
    private static final Long thresholdMs = 1000L;

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        System.out.println("Initiating the task dispatcher");

        // 3 producers
        for (int i = 0; i < 3; i++) {
            new Thread(new Producer(tasksCounter, queue, tasksState), "Task producer - " + i).start();
        }

        // 5 consumers
        for (int i = 0; i < 6; i++) {
            executor.submit(new Consumer(queue, tasksState, activityTracker));
        }

        // monitor thread
        Thread watchdog = new Thread(new Watchdog(activityTracker, thresholdMs, queue, tasksState), "Watchdog");
        watchdog.start();
    }
}