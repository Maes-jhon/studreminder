package com.example.studreminder;

public class Todo {

    private int id;
    private int reviewId;
    private String task;
    private boolean completed;
    private String description;
    private String subTodos;
    private String deadline;
    private String status;
    private boolean isArchived;
    private String label;
    private String labelType; // "CUSTOM" or "SUBJECT"
    private int color;

    public Todo(
            int id,
            int reviewId,
            String task,
            boolean completed,
            String description,
            String subTodos,
            String deadline,
            String status,
            boolean isArchived,
            String label,
            String labelType,
            int color
    ) {
        this.id = id;
        this.reviewId = reviewId;
        this.task = task;
        this.completed = completed;
        this.description = description;
        this.subTodos = subTodos;
        this.deadline = deadline;
        this.status = status;
        this.isArchived = isArchived;
        this.label = label;
        this.labelType = labelType;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public int getReviewId() {
        return reviewId;
    }

    public String getTask() {
        return task;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getDescription() {
        return description;
    }

    public String getSubTodos() {
        return subTodos;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getStatus() {
        return status;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabelType() {
        return labelType;
    }

    public void setLabelType(String labelType) {
        this.labelType = labelType;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}