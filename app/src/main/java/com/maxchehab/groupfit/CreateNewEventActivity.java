package com.maxchehab.groupfit;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.vision.text.Text;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.location.Location.FORMAT_DEGREES;
import static android.location.Location.FORMAT_SECONDS;

/**
 * A login screen that offers login via email/password.
 */
public class CreateNewEventActivity extends AppCompatActivity{


    private Spinner activitySpinner;
    private Button selectTime;
    private Button selectDate;
    private Button selectLocation;
    private ImageView locationImage;
    private SeekBar occupancySeekBar;
    private TextView occupancyText;
    private Button submitForm;
    private AutoCompleteTextView titleInput;
    private AutoCompleteTextView descInput;
    private ProgressBar progressBar;

    public CreateEventForm eventForm = new CreateEventForm(this);


    int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_event);

        titleInput = (AutoCompleteTextView) findViewById(R.id.new_event_title);
        descInput = (AutoCompleteTextView) findViewById(R.id.new_event_description);
        selectTime = (Button) findViewById(R.id.new_event_select_time);
        selectDate = (Button) findViewById(R.id.new_event_select_date);
        selectLocation = (Button) findViewById(R.id.new_event_pick_location);
        activitySpinner = (Spinner) findViewById(R.id.new_event_activity);
        locationImage = (ImageView) findViewById(R.id.new_event_location_image);
        occupancySeekBar = (SeekBar) findViewById(R.id.new_event_occupancy_slider);
        occupancyText = (TextView) findViewById(R.id.new_event_occupancy);
        submitForm = (Button) findViewById(R.id.new_event_submit);
        progressBar = (ProgressBar) findViewById(R.id.create_new_event_progress);


        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        if ( Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();


            Picasso.with(this).load("https://maps.googleapis.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=18&size=800x400&scale=2&maptype=terrain&markers=%7C" + latitude + "," + longitude + "&key=AIzaSyCCadh2fDBt5EI-wgAsBeMIIDQUTWcdSGI").into(locationImage);

        }else{
            Picasso.with(this).load("https://maps.googleapis.com/maps/api/staticmap?center=44.0582, 121.3153&zoom=18&size=800x400&scale=2&maptype=terrain&markers=%7C44.0582, 121.3153&key=AIzaSyCCadh2fDBt5EI-wgAsBeMIIDQUTWcdSGI").into(locationImage);
        }





        selectTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");

            }
        });

        selectDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(),"datePicker");
            }
        });

        selectLocation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               displayPlacePicker(v);

            }
        });

        submitForm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                titleInput.setEnabled(false);
                descInput.setEnabled(false);
                selectTime.setEnabled(false);
                selectDate.setEnabled(false);
                selectLocation.setEnabled(false);
                activitySpinner.setEnabled(false);
                locationImage.setEnabled(false);
                occupancySeekBar.setEnabled(false);
                occupancyText.setEnabled(false);
                submitForm.setEnabled(false);



                eventForm.title = titleInput.getText().toString();
                eventForm.description = descInput.getText().toString();
                eventForm.activity = activitySpinner.getSelectedItem().toString();

                CreateEventFormSuccessor validator = eventForm.submit();
                if(validator.success){
                    finish();

                }else{
                    progressBar.setVisibility(View.GONE);
                    if(!validator.title){
                        titleInput.setError("Title is required");
                    }
                    if(!validator.description){
                        descInput.setError("Description is required");
                    }
                    if(!validator.location){
                        selectLocation.setError("Please select a location");
                    }
                    if(!validator.activity){
                        TextView errorText = (TextView)activitySpinner.getSelectedView();
                        errorText.setError("Please select an activity");
                        errorText.setTextColor(Color.RED);//just to highlight that this is an error
                        errorText.setText("Please select an activity");
                    }
                    if(!validator.date){
                        selectDate.setError("Please select a date");
                    }
                    if(!validator.time){
                        selectTime.setError("Please select a time");
                    }


                    titleInput.setEnabled(true);
                    descInput.setEnabled(true);
                    selectTime.setEnabled(true);
                    selectDate.setEnabled(true);
                    selectLocation.setEnabled(true);
                    activitySpinner.setEnabled(true);
                    locationImage.setEnabled(true);
                    occupancySeekBar.setEnabled(true);
                    occupancyText.setEnabled(true);
                    submitForm.setEnabled(true);

                    /*
                        public boolean title = false;
                        public boolean description = false;
                        public boolean activity = false;
                        public boolean date = false;
                        public boolean time = false;
                        public boolean location = false;
                        public boolean attendeesCount = false;
                        public boolean success = false;
                     */
                }
            }
        });



        locationImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPlacePicker(v);
            }
        });



        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new ArrayList<String>());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add("Select your activity");
        adapter.add("Running");
        adapter.add("Hiking");
        adapter.add("Crossfit");
        adapter.add("Cycling");

        activitySpinner.setAdapter(adapter);
        activitySpinner.setSelection(0);



        occupancySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int people = 3;
            int MIN = 3;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                if (progress < MIN) {

                    occupancyText.setText("Max amount of people: " + people);
                    seekBar.setProgress( MIN);
                } else {
                    people = progress;
                    eventForm.attendeesCount = people;
                }
                occupancyText.setText("Max amount of people: " + people);

            }
        });
    }

    private void displayPlacePicker(View v){
        try{
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            if(eventForm.longitude != null && eventForm.latitude != null){
                builder.setLatLngBounds(new LatLngBounds(new LatLng(eventForm.latitude - 0.005, eventForm.longitude - 0.005),new LatLng(eventForm.latitude + 0.005, eventForm.longitude + 0.005)));

            }
            startActivityForResult(builder.build((Activity)v.getContext()), PLACE_PICKER_REQUEST);
        }catch (GooglePlayServicesRepairableException
                | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void setTime(int hourOfDay, int minute){
        eventForm.time = Integer.toString(hourOfDay) + ":" + Integer.toString(minute);
        if(hourOfDay > 12){
            selectTime.setText(Integer.toString(hourOfDay - 12) + ":" + Integer.toString(minute) + " pm");
        }else{
            selectTime.setText(Integer.toString(hourOfDay) + ":" + Integer.toString(minute) + " am");

        }
        selectTime.setError(null);
    }

    public void setDate(String weekDay, String formatDate){
        eventForm.date = formatDate;
        selectDate.setText(weekDay);
        selectDate.setError(null);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                LatLng latLng = place.getLatLng();
                eventForm.latitude = latLng.latitude;
                eventForm.longitude = latLng.longitude;
                if(place.getName().toString().contains(Integer.toString(eventForm.latitude.intValue())) ){
                    eventForm.addressString = place.getAddress().toString();
                }else{
                    eventForm.addressString = place.getName() + ", " + place.getAddress().toString();
                }

                Log.d("address", eventForm.addressString);
                selectLocation.setError(null);
                Picasso.with(this).load("https://maps.googleapis.com/maps/api/staticmap?center=" +  eventForm.latitude + "," + eventForm.longitude + "&zoom=18&size=800x400&scale=2&maptype=terrain&markers=%7C" +  eventForm.latitude + "," + eventForm.longitude + "&key=AIzaSyCCadh2fDBt5EI-wgAsBeMIIDQUTWcdSGI").into(locationImage);
            }
        }
    }
}

