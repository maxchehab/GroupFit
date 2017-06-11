package com.maxchehab.groupfit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
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

    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit(v.getContext());
            }
        });



        backButton = (ImageButton) findViewById(R.id.expand_back);

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



        for(Person person : FeedActivity.CURRENT_EVENT.attendees){
            LinearLayout mainLayout = (LinearLayout) findViewById(R.id.expand_attendees_list);
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout menuLayout = (LinearLayout) inflater.inflate(R.layout.person_image, mainLayout, true);
            LinearLayout personImageLayout = (LinearLayout) menuLayout.getChildAt(menuLayout.getChildCount() - 1);
            CircularImageView personImage = (CircularImageView) personImageLayout.findViewById(R.id.person_image);

            Log.d("person",person.profileURL + " : " + menuLayout.getChildCount());
            Picasso.with(this).load(person.profileURL).into(personImage);


        }








    }

    private void submit(final Context context){
        submitButton.setEnabled(false);
        final String url = "http://67.204.152.242/groupfit/api/rsvp.php";
        RequestQueue queue = Volley.newRequestQueue(this);


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
                SharedPreferences userDetails = context.getSharedPreferences("user", MODE_PRIVATE);
                String userID = userDetails.getString("userID", "");
                params.put("userID",userID);
                params.put("eventID", FeedActivity.CURRENT_EVENT.eventID);

                return params;
            }
        };
        queue.add(postRequest);
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
