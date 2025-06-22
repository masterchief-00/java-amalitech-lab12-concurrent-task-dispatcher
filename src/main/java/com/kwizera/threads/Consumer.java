package com.kwizera.threads;

import com.kwizera.enums.TaskStatus;
import com.kwizera.models.Task;
import com.kwizera.utils.CustomLogger;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Consumer implements Runnable {
    private final BlockingQueue<Task> queue;
    private final ConcurrentHashMap<UUID, TaskStatus> tasksState;
    private final Map<Thread, Long> activityTracker;
    private static final String GREEN = "\u001B[32m";

    public Consumer(BlockingQueue<Task> queue, ConcurrentHashMap<UUID, TaskStatus> tasksState, Map<Thread, Long> activityTracker) {
        this.queue = queue;
        this.tasksState = tasksState;
        this.activityTracker = activityTracker;
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
            Thread currentThread = Thread.currentThread();
            tasksState.put(task.getId(), TaskStatus.PROCESSING);
            activityTracker.put(currentThread, System.currentTimeMillis());

            CustomLogger.consoleRender(TaskStatus.PROCESSING, "PROCESSING: " + task.getName() + " [" + Thread.currentThread().getName() + "] at [" + task.getCreatedTimestamp() + "]" + "\n" + task.toString());
            Thread.sleep(200);

            tasksState.put(task.getId(), TaskStatus.COMPLETED);
            activityTracker.put(currentThread, System.currentTimeMillis());
            CustomLogger.consoleRender(TaskStatus.COMPLETED, "COMPLETED: " + task.getName() + " [" + Thread.currentThread().getName() + "] at [" + task.getCreatedTimestamp() + "]");
        } catch (InterruptedException e) {
            tasksState.put(task.getId(), TaskStatus.FAILED);
            CustomLogger.consoleRender(TaskStatus.FAILED, "FAILED: " + task.getName() + " [" + Thread.currentThread().getName() + "] at [" + task.getCreatedTimestamp() + "]");
        }
    }
}
