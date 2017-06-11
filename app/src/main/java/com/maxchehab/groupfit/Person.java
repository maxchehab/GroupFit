package com.maxchehab.groupfit;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by maxchehab on 6/11/17.
 */

public class Person{

    public String email = "";
    public String username = "";
    public String profileURL = "";
    public String userID = "";

    public boolean loaded = false;


    private List<Callable<Integer>> callbacks = new ArrayList<>();;

    public Person(final String userID){
        final String url = "http://67.204.152.242/groupfit/api/user_info.php";
        this.userID = userID;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        JsonParser jp = new JsonParser(); //from gson
                        JsonElement root = jp.parse(response); //Convert the input stream to a json element

                        JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.

                        if(rootobj.get("success").getAsBoolean() == true){

                            email = rootobj.get("email").getAsString();
                            username = rootobj.get("username").getAsString();
                            profileURL = rootobj.get("profile").getAsString();

                            loaded = true;
                            executeCallbacks();
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

                params.put("userID", userID);

                return params;
            }
        };
        VolleySingleton.getInstance(ApplicationController.CONTEXT).addToRequestQueue(postRequest);
    }

    private void executeCallbacks(){
        for(Callable<Integer> callback : callbacks){
            try{
                callback.call();
            }catch(Exception e){
                Log.d("Error!",e.toString());
            }

        }
    }

    public void addCallbacks(Callable<Integer> callback){
        if(loaded){
            try{
                callback.call();
            }catch(Exception e){
                Log.d("Error!",e.toString());
            }
        }else{
            callbacks.add(callback);
        }
    }


}
