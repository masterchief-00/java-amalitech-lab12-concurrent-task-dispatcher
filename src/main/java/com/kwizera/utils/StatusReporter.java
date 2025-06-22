package com.kwizera.utils;

import com.kwizera.enums.TaskStatus;
import com.kwizera.models.Task;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class StatusReporter {
    public static void getReport(BlockingQueue<Task> queue, ConcurrentHashMap<UUID, TaskStatus> tasksState) {

        Map<TaskStatus, List<UUID>> groupedByStatus =
                tasksState
                        .entrySet()
                        .stream()
                        .collect(Collectors.groupingBy(
                                Map.Entry::getValue,
                                Collectors.mapping(Map.Entry::getKey,
                                        Collectors.toList())
                        ));

        CustomLogger.appLog(
                CustomLogger.LogLevel.INFO,
                "QUEUE SIZE: " + queue.size() + " - " +
                        "COMPLETED: " + groupedByStatus.entrySet().stream().filter(t -> t.getKey().equals(TaskStatus.COMPLETED)).toList().size() + " - " +
                        "SUBMITTED:" + groupedByStatus.entrySet().stream().filter(t -> t.getKey().equals(TaskStatus.SUBMITTED)).toList().size() + " - " +
                        "FAILED: " + groupedByStatus.entrySet().stream().filter(t -> t.getKey().equals(TaskStatus.FAILED)).toList().size()
        );
    }
}
