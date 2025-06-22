package com.kwizera.models;

import java.time.Instant;
import java.util.UUID;

public class Task implements Comparable<Task> {
    private UUID id;
    private String name;
    private int priority;
    private String payload;
    private Instant createdTimestamp;

    public Task(UUID id, String name, int priority, String payload, Instant createdTimestamp) {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.payload = payload;
        this.createdTimestamp = createdTimestamp;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public void setCreatedTimestamp(Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public int compareTo(Task other) {
        return Integer.compare(this.priority, other.priority);
    }
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", priority=" + priority +
                ", payload='" + payload + '\'' +
                ", createdTimestamp=" + createdTimestamp +
                '}';
    }
}
