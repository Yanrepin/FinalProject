package com.example.finalproject.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.finalproject.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    TextView locationText;
    ImageView newHazard;
    ImageView allInquiries;
    ImageView messages;
    ImageView cityManager;
    String city;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        locationText = findViewById(R.id.userLocation);
        cityManager=findViewById(R.id.inquiriesHandler);
        messages=findViewById(R.id.addNewMassage);
        allInquiries=findViewById(R.id.allInquiries);
        newHazard=findViewById(R.id.newHazard);
        allInquiries.setOnClickListener(this);
        newHazard.setOnClickListener(this);
        cityManager.setOnClickListener(this);
        messages.setOnClickListener(this);

        // Check if the app has the necessary location permission
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission if it is not granted
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION
            );
        } else {
            // Permission is already granted, start location updates
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        // Get the location manager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Handle location updates here
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                // Use Geocoder for reverse geocoding
                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses != null && addresses.size() > 0) {
                        // Get the city name
                        city = addresses.get(0).getLocality();
                        // Update the TextView on the UI thread
                        runOnUiThread(() -> {
                            String locationString = "Your current city is: " + city + "\n";
                            locationText.setText(locationString);
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Handle status changes here
            }
            @Override
            public void onProviderEnabled(String provider) {
                // Handle provider enabled here
            }
            @Override
            public void onProviderDisabled(String provider) {
                // Handle provider disabled here
            }
        };
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Handle the case where permissions are not granted
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                10,
                locationListener
        );
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==newHazard.getId())
        {
            Intent i=new Intent(this,NewHazardActivity.class);
            i.putExtra("cityLocation",city);
            startActivity(i);
        }
        else if(v.getId()==messages.getId())
        {
            Intent i=new Intent(this, MessagesActivity.class);
            i.putExtra("cityLocation",city);
            startActivity(i);
        }
        else if(v.getId()==cityManager.getId())
        {
            Intent i=new Intent(this, CityManagerLoginActivity.class);
            startActivity(i);
        }
        else if(v.getId()==allInquiries.getId())
        {
            Intent i=new Intent(this,InquiriesActivity.class);
            i.putExtra("cityLocation",city);
            startActivity(i);
        }

    }

}
