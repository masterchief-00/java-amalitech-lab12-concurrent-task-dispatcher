package com.kwizera.threads;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kwizera.enums.TaskStatus;
import com.kwizera.models.Task;
import com.kwizera.utils.CustomLogger;
import com.kwizera.utils.StatusReporter;

import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Watchdog implements Runnable {
    private final BlockingQueue<Task> queue;
    private final ConcurrentHashMap<UUID, TaskStatus> tasksState;
    private final Map<Thread, Long> activityTracker;
    private final File outputFile;
    private final Long thresholdMillis;
    private final ObjectMapper mapper = new ObjectMapper();

    private final long jsonReportIntervalMs = 60_000;
    private long lastJsonReportTime = 0;
    private int stuckThreads = 0;

    public Watchdog(Map<Thread, Long> activityTracker, long thresholdMillis, BlockingQueue<Task> queue, ConcurrentHashMap<UUID, TaskStatus> tasksState, File outputFile) {
        this.activityTracker = activityTracker;
        this.thresholdMillis = thresholdMillis;
        this.tasksState = tasksState;
        this.queue = queue;
        this.outputFile = outputFile;
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
                    stuckThreads++;
                }
            }

            // report thread pool status
            StatusReporter.getReport(queue, tasksState);

            if (now - lastJsonReportTime >= jsonReportIntervalMs) {
                try {
                    Map<TaskStatus, Long> groupedByStatus = StatusReporter.groupByStatus(tasksState);
                    long completed = groupedByStatus.getOrDefault(TaskStatus.COMPLETED, 0L);
                    long submitted = groupedByStatus.getOrDefault(TaskStatus.SUBMITTED, 0L);
                    long failed = groupedByStatus.getOrDefault(TaskStatus.FAILED, 0L);

                    ObjectNode report = mapper.createObjectNode();
                    report.put("timestamp", Instant.now().toString());
                    report.put("totalReceived", tasksState.size());
                    report.put("stuck", stuckThreads);
                    report.put("completed", completed);
                    report.put("submitted", submitted);
                    report.put("failed", failed);

                    ArrayNode log;
                    if (outputFile.exists()) {
                        log = (ArrayNode) mapper.readTree(outputFile);
                    } else {
                        log = mapper.createArrayNode();
                    }
                    log.add(report);
                    mapper.writerWithDefaultPrettyPrinter().writeValue(outputFile, log);
                    lastJsonReportTime = now;
                } catch (Exception e) {
                    CustomLogger.appLog(CustomLogger.LogLevel.ERROR, "Failed to write to watchdog JSON file: " + e.getMessage());
                }
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
