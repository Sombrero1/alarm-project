package com.example.myapplication.models;

public class Alarm  {//Для отправки
    private String time;
    private int id;
    private Double[] geo;
    private boolean []days; //массив на 7 элементов
    private boolean selected;

    public Alarm(String time, int id, Double[] geo, boolean[]days, boolean selected) {
        this.time = time;
        this.id = id;
        this.geo = geo;
        this.days = days;
        this.selected = selected;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double[] getGeo() {
        return geo;
    }

    public void setGeo(Double[] geo) {
        this.geo = geo;
    }

    public boolean[] getDays() {
        return days;
    }

    public void setDays(boolean[] days) {
        this.days = days;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
