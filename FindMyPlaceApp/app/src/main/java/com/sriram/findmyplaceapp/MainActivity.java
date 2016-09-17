package com.sriram.findmyplaceapp;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final long MIN_TIME_BW_UPDATES = 1;

    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;

    private LocationManager locationManager;

    private boolean isGPSEnabled;

    private boolean isNetworkEnabled;

    private boolean canGetLocation;

    private Location location;

    private double latitude;

    private double longitude;

    private TextView textView1;

    private TextView textView2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);

    }

    public void findMyLocation() {
        locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);

        // getting GPS status
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            // no network provider is enabled
        } else {
            this.canGetLocation = true;
            if (isNetworkEnabled) {

                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.d("activity", "LOC Network Enabled");
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        Log.d("activity", "LOC by Network");
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.d("activity", "latitude: " + latitude + ", longitude: " + longitude);
                    }
                }
            }
            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                if (location == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("activity", "RLOC: GPS Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            Log.d("activity", "RLOC: loc by GPS");

                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            Log.d("activity", "latitude: " + latitude + ", longitude: " + longitude);

                            textView1.setText("Latitude: " + latitude);
                            textView2.setText("Longitude: " + longitude);
                        }

                    }
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("activity", "onLocationChanged event triggered");
        findMyLocation();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("activity", "onStatusChanged event triggered");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("activity", "onProviderEnabled event triggered");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("activity", "onProviderDisabled event triggered");
    }
}