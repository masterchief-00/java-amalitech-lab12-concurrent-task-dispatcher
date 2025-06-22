package com.kwizera.threads;

import com.kwizera.enums.TaskStatus;
import com.kwizera.models.Task;
import com.kwizera.utils.CustomLogger;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Producer implements Runnable {
    private final AtomicInteger taskCounter;
    private final BlockingQueue<Task> queue;
    private final ConcurrentHashMap<UUID, TaskStatus> tasksState;
    private static final Random random = new Random();
    private static final String YELLOW = "\u001B[33m";

    public Producer(AtomicInteger taskCounter, BlockingQueue<Task> queue, ConcurrentHashMap<UUID, TaskStatus> tasksState) {
        this.queue = queue;
        this.tasksState = tasksState;
        this.taskCounter = taskCounter;
    }

    @Override
    public void run() {
        // create an instance of task
        // add it to queue
        // add it to hashmap
        // wait N ms
        // repeat

        try {
            while (true) {
                UUID id = UUID.randomUUID();
                String name = "Task number - " + taskCounter.incrementAndGet();
                int priority = random.nextInt(3);
                Instant timestamp = Instant.now();
                String payload = "This the instructions of task TSK" + random.nextInt();
                Task newTask = new Task(id, name, priority, payload, timestamp);

                queue.put(newTask);
                tasksState.put(id, TaskStatus.SUBMITTED);
                CustomLogger.consoleRender(TaskStatus.SUBMITTED, "SUBMITTED: " + name + " [" + Thread.currentThread().getName() + "] at [" + newTask.getCreatedTimestamp() + "]" + "\n" + newTask.toString());
                Thread.sleep(300);
            }
        } catch (InterruptedException ignored) {
        }

    }
}
