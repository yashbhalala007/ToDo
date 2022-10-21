package com.example.todo;

public class AllRequest {

    private String task, time, date;

    public AllRequest() {
    }

    public AllRequest(String task, String date, String time) {
        this.task = task;
        this.time = time;
        this.date = date;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
