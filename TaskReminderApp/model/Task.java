package com.example.taskreminderapp.model;

public class Task {
    private long id;
    private String title;
    private String description;
    private long dueDate;
    private boolean completed;

    public Task(long id, String title, String description, long dueDate, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = completed;
    }

    // Getters
    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public long getDueDate() { return dueDate; }
    public boolean isCompleted() { return completed; }
}