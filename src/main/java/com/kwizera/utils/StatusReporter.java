package com.kwizera.utils;

import com.kwizera.enums.TaskStatus;
import com.kwizera.models.Task;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StatusReporter {
    public static void getReport(BlockingQueue<Task> queue, ConcurrentHashMap<UUID, TaskStatus> tasksState) {

        Map<TaskStatus, Long> groupedByStatus = groupByStatus(tasksState);

        CustomLogger.appLog(
                CustomLogger.LogLevel.INFO,
                "QUEUE SIZE: " + queue.size() + " - " +
                        "TOTAL RECEIVED : " + tasksState.size() + " - " +
                        "COMPLETED: " + groupedByStatus.getOrDefault(TaskStatus.COMPLETED, 0L) + " - " +
                        "SUBMITTED: " + groupedByStatus.getOrDefault(TaskStatus.SUBMITTED, 0L) + " - " +
                        "FAILED: " + groupedByStatus.getOrDefault(TaskStatus.FAILED, 0L)
        );
    }

    public static Map<TaskStatus, Long> groupByStatus(ConcurrentHashMap<UUID, TaskStatus> tasksState) {
        return tasksState
                .values().stream()
                .collect(Collectors.groupingBy(
                        Function.identity(),       // Group by the enum value
                        Collectors.counting()      // Count how many per group
                ));
    }
}
