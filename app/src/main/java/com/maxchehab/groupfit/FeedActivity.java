package com.maxchehab.groupfit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.AsyncLayoutInflater;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class FeedActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static Event CURRENT_EVENT;

    SwipeRefreshLayout swipeContainer;


    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ApplicationController.set(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ApplicationController.CONTEXT, CreateNewEventActivity.class);
                ApplicationController.CONTEXT.startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.feed_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);







        /*// Convert to a JSON object to print data
        */


        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                refreshStream();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        
    }

    @Override
    protected void onResume(){
       // Runtime.getRuntime().gc();

        refreshStream();
        updateUserData();

        super.onResume();

    }



    public void updateUserData(){
        SharedPreferences userDetails = this.getSharedPreferences("user", MODE_PRIVATE);
        String userID = userDetails.getString("userID", "");
        final Person user = new Person(userID);
        user.addCallbacks(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                View headerLayout =  navigationView.getHeaderView(0);
                Picasso.with(ApplicationController.CONTEXT).load(user.profileURL).into((CircularImageView) headerLayout.findViewById(R.id.profileImage));

                ((TextView)headerLayout.findViewById(R.id.nav_username)).setText(user.username);
                ((TextView)headerLayout.findViewById(R.id.nav_email)).setText(user.email);

                return null;
            }
        });

    }

    public void refreshStream(){


        final String url = "http://67.204.152.242/groupfit/api/feed.php";



        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        Log.d("response", response);
                        ArrayList<View> stream = new ArrayList<View>();

                        JsonParser jp = new JsonParser(); //from gson
                        JsonElement root = jp.parse(response); //Convert the input stream to a json element

                        JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
                        if(rootobj.get("success").getAsBoolean()){
                            JsonArray jsonStream = rootobj.get("stream").getAsJsonArray(); //just grab the zipcode

                            for(int i = 0; i < jsonStream.size(); i++){
                                JsonObject activity = (JsonObject) jsonStream.get(i);
                                Type collectionType = new TypeToken<String[]>(){}.getType();
                                Gson gson = new Gson();
                                String[] attendeesID = gson.fromJson(activity.get("attendeesID"),collectionType);
                                stream.add(new CardLayout(ApplicationController.CONTEXT, new Event(
                                        activity.get("title").getAsString(),
                                        activity.get("description").getAsString(),
                                        activity.get("activity").getAsString(),
                                        activity.get("hostID").getAsString(),
                                        activity.get("date").getAsString(),
                                        activity.get("time").getAsString(),
                                        activity.get("longitude").getAsDouble(),
                                        activity.get("latitude").getAsDouble(),
                                        activity.get("addressString").getAsString(),
                                        activity.get("attendeesCount").getAsInt(),
                                        activity.get("maxAttendees").getAsInt(),
                                        attendeesID,
                                        activity.get("eventID").getAsString(),
                                        activity.get("createdDate").getAsDouble()

                                        )));
                            }

                            LinearLayout feedLayout = (LinearLayout) findViewById(R.id.layout_feed);

                            feedLayout.removeAllViews();


                            for(int i = 0; i < stream.size(); i++){
                                feedLayout.addView(stream.get(i));
                            }
                        }


                        swipeContainer.setRefreshing(false);





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
                SharedPreferences userDetails = ApplicationController.CONTEXT.getSharedPreferences("user", MODE_PRIVATE);
                String userID = userDetails.getString("userID", "");
                params.put("userID", userDetails.getString("userID",""));

                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(postRequest);



    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.feed_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_crossfit) {

        } else if (id == R.id.nav_cycling) {

        } else if (id == R.id.nav_running) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {
            this.getSharedPreferences("user", 0).edit().clear().commit();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.feed_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
