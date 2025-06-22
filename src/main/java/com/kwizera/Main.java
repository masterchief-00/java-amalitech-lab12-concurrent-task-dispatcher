package com.kwizera;

import com.kwizera.enums.TaskStatus;
import com.kwizera.models.Task;

import java.util.UUID;
import java.util.concurrent.*;

public class Main {
    private final BlockingQueue<Task> queue = new PriorityBlockingQueue<>();
    private final ConcurrentHashMap<UUID, TaskStatus> tasksState = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        ExecutorService executor= Executors.newFixedThreadPool(5);

    }
}