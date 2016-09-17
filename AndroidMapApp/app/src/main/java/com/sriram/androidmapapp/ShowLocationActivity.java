package com.sriram.androidmapapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class ShowLocationActivity extends AppCompatActivity implements LocationListener {

    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;

    private TextView latitudeField;
    private TextView longitudeField;
    private LocationManager locationManager;
    private String provider;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_location);
        latitudeField = (TextView) findViewById(R.id.TextView02);
        longitudeField = (TextView) findViewById(R.id.TextView04);

        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION,getApplicationContext(),CopyOfMap.this)) {
            fetchLocationData();
        }
        else
        {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION,PERMISSION_REQUEST_CODE_LOCATION,getApplicationContext(),CopyOfMap.this);
        }
    }

    public void requestPermission(String strPermission,int perCode,Context _c,Activity _a){

        if (ActivityCompat.shouldShowRequestPermissionRationale(_a,strPermission)){
            Toast.makeText(getApplicationContext(),"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();
        } else {

            ActivityCompat.requestPermissions(_a,new String[]{strPermission},perCode);
        }
    }

    public static boolean checkPermission(String strPermission,Context _c,Activity _a){
        int result = ContextCompat.checkSelfPermission(_c, strPermission);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_REQUEST_CODE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    fetchLocationData();

                } else {

                    Toast.makeText(getApplicationContext(),"Permission Denied, You cannot access location data.",Toast.LENGTH_LONG).show();

                }
                break;

        }
    }


    private void fetchLocationData()
    {
        //code to use the granted permission (location)
    }


























    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Log.d("MapDebug", "onResume - Map permission set");
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_RQUEST_CODE);
//            Log.d("MapDebug", "onResume - If block");
//        }
//        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Log.d("MapDebug", "onPause - Map permission set");
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_RQUEST_CODE);
//            Log.d("MapDebug", "onPause - If block");
//        }
//        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        int lat = (int) (location.getLatitude());
        int lng = (int) (location.getLongitude());
        latitudeField.setText(String.valueOf(lat));
        longitudeField.setText(String.valueOf(lng));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }
}