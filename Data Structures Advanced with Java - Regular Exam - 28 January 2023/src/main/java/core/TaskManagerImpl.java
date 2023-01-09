package core;

import models.Task;

import java.util.*;
import java.util.stream.Collectors;

public class TaskManagerImpl implements TaskManager {

    private final Map<String, Task> tasks;
    private final Map<String, Task> freeTasks;
    private final Map<String, Task> executedTasks;
    private final Queue<Task> taskQueue;

    public TaskManagerImpl() {
        this.tasks = new LinkedHashMap<>();
        this.taskQueue = new ArrayDeque<>();
        this.executedTasks = new HashMap<>();
        this.freeTasks = new LinkedHashMap<>();
    }

    @Override
    public void addTask(Task task) {
        this.tasks.putIfAbsent(task.getId(), task);
        this.freeTasks.putIfAbsent(task.getId(), task);
        this.taskQueue.offer(task);
    }

    @Override
    public boolean contains(Task task) {
        return this.tasks.containsKey(task.getId());
    }

    @Override
    public int size() {
        return this.tasks.size();
    }

    @Override
    public Task getTask(String taskId) {
        if (!this.tasks.containsKey(taskId)) {
            throw new IllegalArgumentException();
        }
        return this.tasks.get(taskId);
    }

    @Override
    public void deleteTask(String taskId) {
        if (!this.tasks.containsKey(taskId)) {
            throw new IllegalArgumentException();
        }
        Task task = this.tasks.remove(taskId);
        this.freeTasks.remove(taskId);
        this.executedTasks.remove(taskId);
        this.taskQueue.remove(task);
    }

    @Override
    public Task executeTask() {
        if (this.taskQueue.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Task task = this.taskQueue.poll();
        this.freeTasks.remove(task.getId());
        this.executedTasks.putIfAbsent(task.getId(), task);
        return task;
    }

    @Override
    public void rescheduleTask(String taskId) {
        if (!this.executedTasks.containsKey(taskId)) {
            throw new IllegalArgumentException();
        }
        Task task = this.executedTasks.get(taskId);
        this.taskQueue.offer(task);
        this.executedTasks.remove(taskId);
    }

    @Override
    public Iterable<Task> getDomainTasks(String domain) {
        List<Task> taskList = this.freeTasks.values()
                .stream()
                .filter(task -> task.getDomain().equals(domain))
                .collect(Collectors.toList());
        if (taskList.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return taskList;
    }

    //7 - Tests correctness
    @Override
    public Iterable<Task> getTasksInEETRange(int lowerBound, int upperBound) {
        return this.taskQueue.stream()
                .filter(task -> task.getEstimatedExecutionTime() >= lowerBound && task.getEstimatedExecutionTime() <= upperBound)
                .collect(Collectors.toList());
    }

    //7, 9, 10
    @Override
    public Iterable<Task> getAllTasksOrderedByEETThenByName() {
        return this.tasks.values()
                .stream()
                .sorted((o1, o2) -> {
                    if (o2.getEstimatedExecutionTime() == o1.getEstimatedExecutionTime()) {
                        return o1.getName().length() - o2.getName().length();
                    }
                    return Integer.compare(o2.getEstimatedExecutionTime(), o1.getEstimatedExecutionTime());
                })
                .collect(Collectors.toList());
    }
}
