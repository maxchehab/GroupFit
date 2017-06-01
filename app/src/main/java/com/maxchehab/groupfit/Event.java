package com.maxchehab.groupfit;

/**
 * Created by Max on 5/10/2017.
 */

public class Event {

    public double longitude;
    public double latitude;
    public String title;
    public String time;
    public String host;
    public int attendeesCount;
    public int remainingCount;
    public String date;
    public String activity;
    public String description;



    public Event( String title, double longitude, double latitude, String activity, String description, String date, String time, String host, int attendeesCount, int remainingCount){
        this.longitude = longitude;
        this.latitude = latitude;
        this.title = title;
        this.date = date;
        this.activity = activity;
        this.description = description;
        this.time = time;
        this.host = host;
        this.attendeesCount = attendeesCount;
        this.remainingCount = remainingCount;
    }
}
