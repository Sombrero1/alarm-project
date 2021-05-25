package com.example.myapplication.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Alarm {//Для отправки
    private String name;
    private int id;
    private Double[] geo;
    private boolean []days; //массив на 7 элементов
    private boolean selected;

    public Alarm(String name, int id, Double[] geo, boolean[]days,boolean selected) {
        this.name = name;
        this.id = id;
        this.geo = geo;
        this.days = days;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
