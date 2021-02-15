package com.example.myapplication.Adapter;

import java.io.Serializable;

public class Alarm_clock_item implements Serializable {
    private int id;
    private String time;
    private long timeMil;
    private String days;
    private boolean checkbox;

    public Alarm_clock_item(String time,long timeMil, String days, boolean checkbox){

        this.time = time;
        this.timeMil = timeMil;
        this.days = days;
        this.checkbox = checkbox;
    }
    public Alarm_clock_item(int id,String time,long timeMil, String days, boolean checkbox){
        this.id = id;
        this.time = time;
        this.timeMil = timeMil;
        this.days = days;
        this.checkbox = checkbox;
    }
    public int getId(){
        return id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public boolean isCheckbox() {
        return checkbox;
    }

    public void setCheckbox(boolean checkbox) {
        this.checkbox = checkbox;
    }

    public long getTimeMil() {
        return timeMil;
    }

    public void setTimeMil(long timeMil) {
        this.timeMil = timeMil;
    }

    public void setId(int id) {
        this.id = id;
    }
}
