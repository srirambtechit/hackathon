package com.sriram.rbsbankoffers;


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.sriram.rbsbankoffers.util.AppUtil;

import java.text.DateFormat;
import java.util.Date;


/**
 * Created by srirammuthaiah on 9/17/16.
 */
public class CustomerLocationProvider implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
//    private static final int MY_ACCESS_PERMISSION_LOCATION = 1;

    private Context context;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private String mLastUpdateTime;

    private boolean permissionGranted = true;

    public CustomerLocationProvider(Context context, GoogleApiClient mGoogleApiClient) {
        this.context = context;

        createLocationRequest();

        this.mGoogleApiClient = mGoogleApiClient;
        mGoogleApiClient.registerConnectionCallbacks(this);
        mGoogleApiClient.registerConnectionFailedListener(this);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public String getLocationUpdatedTime() {
        return mLastUpdateTime;
    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    public boolean isLocationAccessGranted() {
        return permissionGranted;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(AppUtil.TAG, "Firing onLocationChanged");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(AppUtil.TAG, "GoogleApiClient onConnected - isConnected: " + mGoogleApiClient.isConnected());

//        if (ActivityCompat.checkSelfPermission(HomeActivity.getInstance().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(HomeActivity.getInstance().getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.getInstance(), android.Manifest.permission.ACCESS_COARSE_LOCATION) ||
//                    ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.getInstance(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {
//                //If the user has denied the permission previously your code will come to this block
//                //Here you can explain why you need this permission
//                //Explain here why you need this permission
//            } else {
//                ActivityCompat.requestPermissions(HomeActivity.getInstance(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_ACCESS_PERMISSION_LOCATION);
//            }
//            return;
//        }

        Log.d(AppUtil.TAG, "Context captured: " + context);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Making connection and registering for location updates
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Log.d(AppUtil.TAG, "Location update started");
        }else {
            permissionGranted = false;
            Log.d(AppUtil.TAG, "onConnected - location permission is missing");
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(AppUtil.TAG, "GoogleApiClient onConnectionSuspended: " + mGoogleApiClient.isConnected());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(AppUtil.TAG, "Connection failed: " + connectionResult.toString());
    }
}
