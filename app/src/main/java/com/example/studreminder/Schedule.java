package com.example.studreminder;

public class Schedule {
    private int id;
    private String subject;
    private String date;
    private String startTime;
    private String endTime;
    private String room;
    private int color;
    private String reminder;

    public Schedule(int id, String subject, String date, String startTime, String endTime, String room, int color, String reminder) {
        this.id = id;
        this.subject = subject;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.color = color;
        this.reminder = reminder;
    }

    public int getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getRoom() {
        return room;
    }

    public int getColor() {
        return color;
    }

    public String getReminder() {
        return reminder;
    }
}