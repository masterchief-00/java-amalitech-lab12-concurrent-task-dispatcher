package com.kwizera.threads;

import com.kwizera.enums.TaskStatus;
import com.kwizera.models.Task;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Producer implements Runnable {
    private final BlockingQueue<Task> queue;
    private final ConcurrentHashMap<UUID, TaskStatus> tasksState;

    public Producer(BlockingQueue<Task> queue, ConcurrentHashMap<UUID, TaskStatus> tasksState) {
        this.queue = queue;
        this.tasksState = tasksState;
    }

    @Override
    public void run() {
        // create an instance of task
        // add it to queue
        // add it to hashmap
        // wait N ms
        // repeat
    }
}
