package com.maxchehab.groupfit;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.text.Text;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

public class ExpandedEventActivity extends AppCompatActivity implements OnMapReadyCallback {


    private TextView title;
    private TextView date;
    private TextView time;
    private TextView location;
    private TextView host;
    private TextView attendees;
    private TextView description;
    private GestureDetector gestureDetector;
    private Toolbar toolbar;
    private ScrollView scrollView;
    private Button submitButton;
    private TextView declineMessage;
    private TextView declineLink;
    private TextView fullMessage;
    private TextView deleteMessage;
    private TextView deleteLink;

    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ApplicationController.set(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expanded_event);

        toolbar = (Toolbar) findViewById(R.id.expand_toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.expand_mapView);
        mapFragment.getMapAsync(this);

        gestureDetector = new GestureDetector(this, new GestureListener());


        setTitle("");
        title = (TextView) findViewById(R.id.expand_title);
        date = (TextView) findViewById(R.id.expand_date);
        description = (TextView) findViewById(R.id.expand_description);
        time = (TextView) findViewById(R.id.expand_time);
        location = (TextView) findViewById(R.id.expand_location);
        host = (TextView) findViewById(R.id.expand_host);
        attendees = (TextView) findViewById(R.id.expand_attendees);
        scrollView = (ScrollView) findViewById(R.id.expand_scrollView);
        submitButton = (Button) findViewById(R.id.expand_signup);
        declineMessage = (TextView) findViewById(R.id.expand_decline_message);
        declineLink = (TextView) findViewById(R.id.expand_decline_link);
        fullMessage = (TextView) findViewById(R.id.expand_full_message);
        deleteMessage = (TextView) findViewById(R.id.expand_delete_message);
        deleteLink = (TextView) findViewById(R.id.expand_delete_link);

        declineLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Decline RSVP")
                        .setMessage("Are you sure you want to decline this activity?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                decline();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });
        declineMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Decline RSVP")
                        .setMessage("Are you sure you want to decline this activity?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                decline();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        deleteLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete activity")
                        .setMessage("Are you sure you want to delete this activity?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delete();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        deleteMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete activity")
                        .setMessage("Are you sure you want to delete this activity?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delete();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });



        backButton = (ImageButton) findViewById(R.id.expand_back);



        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY - oldScrollY > 10) {
                    hideToolbar();
                }
                if (scrollY - oldScrollY  < -10) {
                    showToolbar();
                }

                if (scrollY == 0) {
                    showToolbar();
                }
            }
        });

        refresh();

    }

    private void delete(){
        final String url = "http://67.204.152.242/groupfit/api/delete.php";



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

                            finish();
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
                SharedPreferences userDetails = ApplicationController.CONTEXT.getSharedPreferences("user", MODE_PRIVATE);
                String userID = userDetails.getString("userID", "");
                Map<String, String>  params = new HashMap<String, String>();
                params.put("eventID", FeedActivity.CURRENT_EVENT.eventID);
                params.put("userID", userID);

                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(postRequest);
    }


    private void decline(){
        final String url = "http://67.204.152.242/groupfit/api/decline.php";



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
                            refresh();

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
                SharedPreferences userDetails = ApplicationController.CONTEXT.getSharedPreferences("user", MODE_PRIVATE);
                String userID = userDetails.getString("userID", "");
                Map<String, String>  params = new HashMap<String, String>();
                params.put("eventID", FeedActivity.CURRENT_EVENT.eventID);
                params.put("userID", userID);

                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(postRequest);
    }

    private void refresh(){
        final String url = "http://67.204.152.242/groupfit/api/event.php";



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
                            JsonObject activity = rootobj.get("event").getAsJsonObject(); //just grab the zipcode

                            Type collectionType = new TypeToken<String[]>(){}.getType();
                            Gson gson = new Gson();
                            String[] attendeesID = gson.fromJson(activity.get("attendeesID"),collectionType);
                            FeedActivity.CURRENT_EVENT = new Event(
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

                            );

                            updateValues();

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
                params.put("eventID", FeedActivity.CURRENT_EVENT.eventID);

                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(postRequest);
    }

    private void updateValues(){
        title.setText(FeedActivity.CURRENT_EVENT.title);


        date.setText(FeedActivity.CURRENT_EVENT.date);

        time.setText(FeedActivity.CURRENT_EVENT.time);
        location.setText(FeedActivity.CURRENT_EVENT.addressString);
        FeedActivity.CURRENT_EVENT.host.addCallbacks(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                host.setText(FeedActivity.CURRENT_EVENT.host.username);
                return null;
            }
        });
        attendees.setText(FeedActivity.CURRENT_EVENT.attendeesCount + " people are going • " + (FeedActivity.CURRENT_EVENT.maxAttendees - FeedActivity.CURRENT_EVENT.attendeesCount) +  " spots left.");
        description.setText(FeedActivity.CURRENT_EVENT.description);

        boolean involved = false;


        ((LinearLayout)findViewById(R.id.expand_attendees_list)).removeAllViews();
        SharedPreferences userDetails = this.getSharedPreferences("user", MODE_PRIVATE);
        String userID = userDetails.getString("userID", "");
        for(final Person person : FeedActivity.CURRENT_EVENT.attendees){
            if(person.userID.equals(userID)){
                involved = true;
            }
            LinearLayout mainLayout = (LinearLayout) findViewById(R.id.expand_attendees_list);
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout menuLayout = (LinearLayout) inflater.inflate(R.layout.person_image, mainLayout, true);
            LinearLayout personImageLayout = (LinearLayout) menuLayout.getChildAt(menuLayout.getChildCount() - 1);
            final CircularImageView personImage = (CircularImageView) personImageLayout.findViewById(R.id.person_image);

            Log.d("person",person.profileURL + " : " + menuLayout.getChildCount());
            person.addCallbacks(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    Picasso.with(ApplicationController.CONTEXT).load(person.profileURL).into(personImage);
                    return null;
                }
            });
        }
        if(userID.equals(FeedActivity.CURRENT_EVENT.host.userID)){
            deleteMessage.setVisibility(View.VISIBLE);
            deleteLink.setVisibility(View.VISIBLE);
            declineMessage.setVisibility(View.GONE);
            declineLink.setVisibility(View.GONE);
            submitButton.setVisibility(View.GONE);
            fullMessage.setVisibility(View.GONE);
        }else if(involved){
            declineMessage.setVisibility(View.VISIBLE);
            declineLink.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.GONE);
            fullMessage.setVisibility(View.GONE);
            deleteMessage.setVisibility(View.GONE);
            deleteLink.setVisibility(View.GONE);
        }else if((FeedActivity.CURRENT_EVENT.maxAttendees - FeedActivity.CURRENT_EVENT.attendeesCount) < 1){
            declineMessage.setVisibility(View.GONE);
            declineLink.setVisibility(View.GONE);
            submitButton.setVisibility(View.GONE);
            fullMessage.setVisibility(View.VISIBLE);
            deleteMessage.setVisibility(View.GONE);
            deleteLink.setVisibility(View.GONE);
        }else{
            declineMessage.setVisibility(View.GONE);
            declineLink.setVisibility(View.GONE);
            submitButton.setVisibility(View.VISIBLE);
            fullMessage.setVisibility(View.GONE);
            deleteLink.setVisibility(View.GONE);
            deleteMessage.setVisibility(View.GONE);
        }
    }

    private void submit(){
        submitButton.setEnabled(false);
        final String url = "http://67.204.152.242/groupfit/api/rsvp.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response", response);
                        /*JsonParser jp = new JsonParser(); //from gson
                        JsonElement root = jp.parse(response); //Convert the input stream to a json element

                        JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
                        */
                        submitButton.setEnabled(true);
                        refresh();
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
                SharedPreferences userDetails = ExpandedEventActivity.this.getSharedPreferences("user", MODE_PRIVATE);
                String userID = userDetails.getString("userID", "");
                params.put("userID",userID);
                params.put("eventID", FeedActivity.CURRENT_EVENT.eventID);

                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(postRequest);
    }

    public void hideToolbar(){
        findViewById(R.id.expand_appBarLayout).animate().translationY(-toolbar.getHeight()).setDuration(100);
    }
    public void showToolbar(){
        findViewById(R.id.expand_appBarLayout).animate().translationY(0).setDuration(100);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.expanded_event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
      //  if (id == R.id.action_settings) {
        //    return true;
       // }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(gestureDetector.onTouchEvent(ev)){
           // scaleView(findViewById(R.id.expand_checkAnimation),400);
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }


    public void scaleView(final View v, int milliseconds) {

        v.animate().scaleX(1).withEndAction(new Runnable() {
            @Override
            public void run() {
                v.setScaleX(v.getScaleX()-1);
            }
        }).setDuration(milliseconds).start();
        v.animate().scaleY(1).withEndAction(new Runnable() {
            @Override
            public void run() {
                v.setScaleY(v.getScaleY()-1);
            }
        }).setDuration(milliseconds).start();




    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Add a marker in Sydney and move the camera
        LatLng event = new LatLng(FeedActivity.CURRENT_EVENT.latitude, FeedActivity.CURRENT_EVENT.longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(event, 18.0f));
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(event)
                .title(FeedActivity.CURRENT_EVENT.title));
        marker.showInfoWindow();


        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        //intent:43.997128,-121.404549?q=43.997128,-121.404549(Hello world)#Intent;scheme=geo;launchFlags=0x3000000;end
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            super.onShowPress(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            Log.d("Double Tap", "Tapped at: (" + x + "," + y + ")");

            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onContextClick(MotionEvent e) {
            return false;
        }
    }
}
