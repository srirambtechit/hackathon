package com.sriram.offerbankerapp;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    protected Context context;

    protected LocationListener locationListener;

    protected LocationManager locationManager;

    TextView txtLat;
    String lat;
    String provider;

    protected String latitude, longitude;
    protected boolean gps_enabled, network_enabled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Sending message to background running service
//        Intent intent = new Intent(this, OfferDataPullIntentService.class);
//        intent.putExtra("message", "Application Main Activity started successfully");
//        startService(intent);

        txtLat = (TextView) findViewById(R.id.textView2);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Define the criteria how to select the location provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            Log.d("MapDebug", "Location not available");
            Log.d("MapDebug", "Location not available");
        }


//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            Log.d("MapDebug", "first line");
//
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                Log.d("MapDebug", "2nd line");
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//            } else {
//                Log.d("MapDebug", "3rd line");
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
//                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
//
//        }

        Log.d("MapDebug", "after if block line");

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);


//        scheduleAlarm();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Setup a recurring alarm every half hour
    public void scheduleAlarm() {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), DataAlarmReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, DataAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every 5 seconds
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                //AlarmManager.INTERVAL_FIFTEEN_MINUTES, pIntent);
                10000, pIntent);
    }

    public void cancelAlarm() {
        Intent intent = new Intent(getApplicationContext(), DataAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, DataAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("MapDebug", "onLocationChange event");
        txtLat = (TextView) findViewById(R.id.textView2);
        txtLat.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d("Latitude", "Status");
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d("Latitude", "disable");
    }
}