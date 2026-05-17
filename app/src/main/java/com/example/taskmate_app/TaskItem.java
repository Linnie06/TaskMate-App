package com.example.taskmate_app;

public class TaskItem {
    private String name;
    private Long dueAt;
    private String priority;
    private boolean completed;
    private String id;

    public TaskItem(String name, Long dueAt, String priority, boolean completed, String id) {
        this.name = name;
        this.dueAt = dueAt;
        this.priority = priority;
        this.completed = completed;
        this.id = id;
    }

    // Getters
    public String getName() { return name; }
    public Long getDueAt() { return dueAt; }
    public String getPriority() { return priority; }
    public boolean isCompleted() { return completed; }
    public String getId() { return id; }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setDueAt(Long dueAt) {
        this.dueAt = dueAt;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setId(String id) {
        this.id = id;
    }
}