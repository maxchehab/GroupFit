package com.maxchehab.groupfit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by maxchehab on 6/10/17.
 */

public class CreateEventForm {

    public String title;
    public String description;
    public String activity;
    public String date;
    public String time;
    public Double longitude;
    public Double latitude;
    public String addressString;
    public Integer attendeesCount = 3;

    private transient CreateEventFormSuccessor successorValidator = new CreateEventFormSuccessor();

    private Activity parentActivity;

    public CreateEventForm(Activity activity){
        this.parentActivity = activity;
    }


    public CreateEventFormSuccessor submit(){
        if(!title.isEmpty()){
            successorValidator.title = true;
        }
        if(!description.isEmpty()){
            successorValidator.description = true;
        }
        if(!activity.isEmpty() && activity != "Select your activity"){
            successorValidator.activity = true;
        }
        if(time != null){
            successorValidator.time = true;
        }
        if(date!= null){
            successorValidator.date = true;
        }
        if(longitude != null && latitude != null && !addressString.isEmpty()){
            successorValidator.location = true;
        }
        if(attendeesCount != null){
            successorValidator.attendeesCount = true;
        }

        if(!checkCompleteness()){
            return successorValidator;
        }



        String url = "http://67.204.152.242/groupfit/api/create_event.php";
        RequestQueue queue = Volley.newRequestQueue(parentActivity);


        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);

                        JsonParser jp = new JsonParser(); //from gson
                        JsonElement root = jp.parse(response); //Convert the input stream to a json element

                        JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.

                        if(rootobj.get("success").getAsBoolean() == true){
                            parentActivity.finish();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

                SharedPreferences userDetails = parentActivity.getSharedPreferences("user", MODE_PRIVATE);
                params.put("hostID", userDetails.getString("userID", ""));
                params.put("title", title);
                params.put("description", description);
                params.put("activity", activity);
                params.put("date", date);
                params.put("time", time);
                params.put("longitude", longitude.toString());
                params.put("latitude", latitude.toString());
                params.put("addressString", addressString);
                params.put("maxAttendees", attendeesCount.toString());

                return params;
            }
        };
        queue.add(postRequest);



        return successorValidator;
    }

    private boolean checkCompleteness (){
        return (successorValidator.location && successorValidator.activity && successorValidator.description && successorValidator.attendeesCount && successorValidator.date && successorValidator.time && successorValidator.title);
    }


}


