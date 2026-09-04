package com.example.studreminder;

public class Subject {

    private int id;
    private String subject;
    private String teacher;
    private String room;
    private int color;

    public Subject(
            int id,
            String subject,
            String teacher,
            String room,
            int color
    ) {
        this.id = id;
        this.subject = subject;
        this.teacher = teacher;
        this.room = room;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getRoom() {
        return room;
    }

    public int getColor() {
        return color;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setColor(int color) {
        this.color = color;
    }
}