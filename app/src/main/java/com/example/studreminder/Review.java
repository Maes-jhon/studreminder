package com.example.studreminder;

public class Review {

    private int id;
    private String subject;
    private String topic;
    private String date;
    private String time;
    private String reminder;
    private String status;
    private boolean isArchived;

    public Review(int id,
                  String subject,
                  String topic,
                  String date,
                  String time,
                  String reminder,
                  String status,
                  boolean isArchived) {

        this.id = id;
        this.subject = subject;
        this.topic = topic;
        this.date = date;
        this.time = time;
        this.reminder = reminder;
        this.status = status;
        this.isArchived = isArchived;
    }

    public int getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getTopic() {
        return topic;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getReminder() {
        return reminder;
    }

    public String getStatus() {
        return status;
    }

    public boolean isArchived() {
        return isArchived;
    }
}