package com.example.myapplication.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Alarm {
    String name;
    int id;
    Double[] geo;

    public Alarm() {
    }

    public Alarm(String name, int id, Double[] geo) {
        this.name = name;
        this.id = id;
        this.geo = geo;
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
}
