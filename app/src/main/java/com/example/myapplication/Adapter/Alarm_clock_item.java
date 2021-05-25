package com.example.myapplication.Adapter;

import java.io.Serializable;

public class Alarm_clock_item implements Serializable {

    private int id;//primary
    private String time;
    private long timeMil;
    private boolean[] checkboxs;
    private Double[] geo;
    private boolean selected;

    public Alarm_clock_item(String time,long timeMil, Double[] geo, boolean[] checkbox,boolean selected){
        this.time = time;
        this.timeMil = timeMil;
        this.checkboxs = checkbox;
        this.geo = geo;
        this.selected = selected;
    }



    public Double[] getGeo() {
        return geo;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getTimeMil() {
        return timeMil;
    }

    public boolean[] getCheckboxs() {
        return checkboxs;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
