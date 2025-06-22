package com.kwizera.threads;

import com.kwizera.enums.TaskStatus;
import com.kwizera.models.Task;
import com.kwizera.utils.CustomLogger;
import com.kwizera.utils.StatusReporter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Watchdog implements Runnable {
    private final BlockingQueue<Task> queue;
    private final ConcurrentHashMap<UUID, TaskStatus> tasksState;
    private final Map<Thread, Long> activityTracker;
    private final Long thresholdMillis;

    public Watchdog(Map<Thread, Long> activityTracker, long thresholdMillis, BlockingQueue<Task> queue, ConcurrentHashMap<UUID, TaskStatus> tasksState) {
        this.activityTracker = activityTracker;
        this.thresholdMillis = thresholdMillis;
        this.tasksState = tasksState;
        this.queue = queue;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            long now = System.currentTimeMillis();
            for (Map.Entry<Thread, Long> entry : activityTracker.entrySet()) {
                Thread thread = entry.getKey();
                long lastActive = entry.getValue();
                if (now - lastActive > thresholdMillis) {
                    CustomLogger.appLog(CustomLogger.LogLevel.ERROR, thread.getName() + " seems to be stuck. It's last activity was " + (now - lastActive) + "ms ago");
                }
            }

            // report thread pool status
            StatusReporter.getReport(queue, tasksState);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
