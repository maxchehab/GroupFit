package com.maxchehab.groupfit;

/**
 * Created by Max on 5/10/2017.
 */

public class Event {

    public double longitude;
    public double latitude;
    public String title;
    public int progress;
    public int remaining;
    public String date;
    public String activity;
    public String description;



    public Event( String title, double longitude, double latitude, int progress, int remaining, String date, String activity, String description){
        this.longitude = longitude;
        this.latitude = latitude;
        this.title = title;
        this.progress = progress;
        this.remaining = remaining;
        this.date = date;
        this.activity = activity;
        this.description = description;
    }
}
