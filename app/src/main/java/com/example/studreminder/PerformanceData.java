package com.example.studreminder;

public class PerformanceData {
    private String day;
    private int durationMinutes;
    private int tasksCompleted;
    private int totalTasks;

    public PerformanceData(String day, int durationMinutes, int tasksCompleted, int totalTasks) {
        this.day = day;
        this.durationMinutes = durationMinutes;
        this.tasksCompleted = tasksCompleted;
        this.totalTasks = totalTasks;
    }

    public String getDay() {
        return day;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public int getTasksCompleted() {
        return tasksCompleted;
    }

    public int getTotalTasks() {
        return totalTasks;
    }
    
    public float getCompletionRate() {
        if (totalTasks == 0) return 0;
        return (float) tasksCompleted / totalTasks;
    }
}
