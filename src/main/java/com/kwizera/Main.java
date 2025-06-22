package com.kwizera;

import com.kwizera.enums.TaskStatus;
import com.kwizera.models.Task;
import com.kwizera.threads.Consumer;
import com.kwizera.threads.Producer;
import com.kwizera.threads.Watchdog;
import com.kwizera.utils.CustomLogger;

import java.nio.file.Path;
import java.nio.file.Paths;
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
    private static final Path outputFile = Paths.get(System.getProperty("user.home"), "task_dispatcher_watchdog.json");

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        System.out.println("Initiating the task dispatcher");

        // 3 producers
        for (int i = 0; i < 3; i++) {
            processWithRetry(new Producer(tasksCounter, queue, tasksState), 3, i);
        }

        // 5 consumers
        for (int i = 0; i < 6; i++) {
            executor.submit(new Consumer(queue, tasksState, activityTracker));
        }

        // monitor thread
        Thread watchdog = new Thread(new Watchdog(activityTracker, thresholdMs, queue, tasksState, outputFile.toFile()), "Watchdog");
        watchdog.start();
    }

    public static void processWithRetry(Producer producer, int maxRetries, int iteratorCounter) {
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                CustomLogger.appLog(CustomLogger.LogLevel.INFO, "Starting thread " + "Task producer - " + iteratorCounter + ". ATTEMPT " + (attempt + 1));
                new Thread(producer, "Task producer - " + iteratorCounter).start();
                return;
            } catch (Exception e) {
                attempt++;
                attempt%=4;
                CustomLogger.appLog(CustomLogger.LogLevel.WARN, "[RETRY] Starting thread " + "Task producer - " + iteratorCounter + ". ATTEMPT " + (attempt + 1) + "/" + maxRetries);
                if (attempt >= maxRetries) {
                    CustomLogger.appLog(CustomLogger.LogLevel.ERROR, "[FAILED] failed to start thread " + "Task producer - " + iteratorCounter);
                    return;
                }
            }
        }

    }
}