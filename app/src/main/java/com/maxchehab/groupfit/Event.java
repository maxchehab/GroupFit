package com.maxchehab.groupfit;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Max on 5/10/2017.
 */

public class Event{

    public String title;
    public String description;
    public String activity;
    public String date;
    public String time;
    public double longitude;
    public double latitude;
    public String addressString;
    public int attendeesCount;
    public int maxAttendees;
    public List<Person> attendees = new ArrayList<>();;
    public String eventID;
    public double createdDate;
    public Person host;

    public Event(String title, String description, String activity, String hostID, String date, String time, double longitude, double latitude, String addressString, int attendeesCount, int maxAttendees, String[] attendeesID, String eventID, double createdDate){
        this.title = title;
        this.description = description;
        this.activity = activity;
        this.host = new Person(hostID);
        try{
            Calendar today = Calendar.getInstance();
            Calendar tommorow = Calendar.getInstance();
            tommorow.add(Calendar.DAY_OF_YEAR, 1);

            Calendar c2 = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH);
            c2.setTime(sdf.parse(date));

            if (today.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && today.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)){
                this.date = "Today";
            }else if(tommorow.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && tommorow.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)){
                this.date = "Tomorrow";
            }else{
                this.date = date;
            }

        }catch(java.text.ParseException e){
            Log.d("error",e.toString());
            this.date = date;
        }

        try{
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            calendar.setTime(sdf.parse(time));

            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            if(hourOfDay > 12){
                this.time = Integer.toString(hourOfDay - 12) + ":" + Integer.toString(minute) + " pm";
            }else{
                this.time = Integer.toString(hourOfDay) + ":" + Integer.toString(minute) + " am";

            }

        }catch(java.text.ParseException e){
            Log.d("error",e.toString());
            this.time = time;
        }


        this.longitude = longitude;
        this.latitude = latitude;
        this.addressString = addressString;
        this.attendeesCount = attendeesCount;
        this.maxAttendees = maxAttendees;
        for (String id : attendeesID) {
            this.attendees.add(new Person(id));
        }
        this.eventID = eventID;
        this.createdDate = createdDate;
    }
}
