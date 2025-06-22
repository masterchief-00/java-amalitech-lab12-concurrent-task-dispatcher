package com.kwizera.threads;

import com.kwizera.enums.TaskStatus;
import com.kwizera.models.Task;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Consumer implements Runnable {
    private final BlockingQueue<Task> queue;
    private final ConcurrentHashMap<UUID, TaskStatus> tasksState;
    private static final String GREEN = "\u001B[32m";

    public Consumer(BlockingQueue<Task> queue, ConcurrentHashMap<UUID, TaskStatus> tasksState) {
        this.queue = queue;
        this.tasksState = tasksState;
    }

    @Override
    public void run() {
        // check the queue for a task
        // if not empty, take() task process it, update its state, log it
        // if empty, block

        try {
            while (true) {
                Task task = queue.take();
                processTask(task);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void processTask(Task task) {
        try {
            tasksState.put(task.getId(), TaskStatus.PROCESSING);
//            System.out.println("Processing " + Thread.currentThread().getName());
            System.out.println(GREEN + "PROCESSING: " + task.getName() + "\n" + task.toString());
            System.out.println("=================================================================");
            Thread.sleep(150);
            tasksState.put(task.getId(), TaskStatus.COMPLETED);
        } catch (InterruptedException e) {
            tasksState.put(task.getId(), TaskStatus.FAILED);
        }
    }
}
