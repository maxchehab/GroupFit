package com.maxchehab.groupfit;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.text.Text;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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



    int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_event);

        selectTime = (Button) findViewById(R.id.new_event_select_time);
        selectDate = (Button) findViewById(R.id.new_event_select_date);
        selectLocation = (Button) findViewById(R.id.new_event_pick_location);
        activitySpinner = (Spinner) findViewById(R.id.new_event_activity);
        locationImage = (ImageView) findViewById(R.id.new_event_location_image);
        occupancySeekBar = (SeekBar) findViewById(R.id.new_event_occupancy_slider);
        occupancyText = (TextView) findViewById(R.id.new_event_occupancy);


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
                }
                occupancyText.setText("Max amount of people: " + people);

            }
        });
    }

    private void displayPlacePicker(View v){
        try{
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

            startActivityForResult(builder.build((Activity)v.getContext()), PLACE_PICKER_REQUEST);
        }catch (GooglePlayServicesRepairableException
                | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void setTime(int hourOfDay, int minute){
        if(hourOfDay > 12){
            selectTime.setText(Integer.toString(hourOfDay - 12) + ":" + Integer.toString(minute) + " pm");
        }else{
            selectTime.setText(Integer.toString(hourOfDay) + ":" + Integer.toString(minute) + " am");

        }
    }

    public void setDate(String weekDay, int year, int month, int day){
        //selectDate.setText(Integer.toString(month) + "/" + Integer.toString(day) + "/" + Integer.toString(year));
        selectDate.setText(weekDay);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                LatLng latLng = place.getLatLng();
                double latitude = latLng.latitude;
                double longitude = latLng.longitude;
                Picasso.with(this).load("https://maps.googleapis.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=18&size=800x400&scale=2&maptype=terrain&markers=%7C" + latitude + "," + longitude + "&key=AIzaSyCCadh2fDBt5EI-wgAsBeMIIDQUTWcdSGI").into(locationImage);

            }
        }
    }
}

