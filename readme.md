# LAB: Concurrent Task Dispatch System

## Overview

A multithreaded task dispatch system that simulates real-world job submission and processing using core Java concurrency
primitives.

## Activity Diagram

![task_dispatcher_activity_diagram.jpg](src%2Fmain%2Fresources%2Ftask_dispatcher_activity_diagram.jpg)

## Features

### Job Queue

- Uses `PriorityBlockingQueue<Task>` to safely coordinate between producers and consumers.

### Producers

- Generate jobs and put them into the queue.

### Consumers

- Run in an `ExecutorService`
- Pull jobs from the queue
- Support **automatic retry** for failed jobs (with configurable max attempts)

### Watchdog

- Monitors last activity timestamp of each consumer thread
- Detects stuck jobs (e.g., running too long)
- Writes a JSON report every 60 seconds:
  ```json
  {
    "timestamp" : "2025-06-22T18:30:18.030496300Z",
    "totalReceived" : 585,
    "stuck" : 0,
    "completed" : 582,
    "submitted" : 0,
    "failed" : 0
  }

## Stack

- Java 21
- Maven (for dependency management)

## How to run

- Clone the project
- Open in intelliJ IDEA or another JavaFX-Compatible IDE
- Maven adds dependencies automatically
- Run the `Main` class
- 👍