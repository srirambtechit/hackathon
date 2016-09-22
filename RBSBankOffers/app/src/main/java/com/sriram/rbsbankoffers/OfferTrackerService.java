package com.sriram.rbsbankoffers;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.sriram.rbsbankoffers.util.AppUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class OfferTrackerService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    // Place holders for location tracker
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private static final long LOCATION_TRACKER_INTERVAL_MINUTES = 1000 * 20; // 20 seconds
    private static final long LOCATION_TRACKER_FASTEST_INTERVAL_MINUTES = 1000 * 10;  //in general, halves of LOCATION_TRACKER_INTERVAL_MINUTES ie. 10 seconds

    // Place holders for Webservice
    private JSONObject json;
    private int success = 0;
    private HTTPURLConnection service;

    // Place holders for notification system
    private static int notificationCounter = 1;

    public OfferTrackerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(AppUtil.TAG, "OfferTrackerService - onCreate");

        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            Log.d(AppUtil.TAG, "GooglePlayService is not available");
            return;
        }

        createLocationRequest();

        // New thread spawned as location tracker service should not affect execution of UI thread
        new Thread(new Runnable() {
            @Override
            public void run() {

                mGoogleApiClient = new GoogleApiClient.Builder(OfferTrackerService.this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(OfferTrackerService.this)
                        .addOnConnectionFailedListener(OfferTrackerService.this)
                        .build();

                // blocking IO for making GoogleAPI to establish connection
                mGoogleApiClient.blockingConnect(30, TimeUnit.SECONDS);


            }
        }).start();

        service = new HTTPURLConnection();

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // when service starts, do my business logic here
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(AppUtil.TAG, "OfferTrackerService - onStartCommand");
        int i = super.onStartCommand(intent, flags, startId);

        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        if (intent != null) {
            String message = "OfferTrackerService started at " + new Date();
            Log.d(AppUtil.TAG, message);

            Log.d(AppUtil.TAG, "OfferTrackerService - onStartCommand - Location: " + mCurrentLocation);

            if (mCurrentLocation != null) {
                String text = null;
                // RESTful webservice call to fetch personalized offer details for the customer
                if (service != null) {
                    new PostDataTOServer().execute();
                }

            }

        }
        return i;
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_TRACKER_INTERVAL_MINUTES);
        mLocationRequest.setFastestInterval(LOCATION_TRACKER_FASTEST_INTERVAL_MINUTES);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
//            GooglePlayServicesUtil.getErrorDialog(status, HomeScreen.class, 0).show();
            return false;
        }
    }

    private void showNotification(String text) {

        Intent fullTextIntent = new Intent(this, HomeScreen.class);
        fullTextIntent.putExtra(AppUtil.NOTIFICATION_FULL_TEXT_INTENT_KEY, text);

        int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, uniqueInt, fullTextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Resources r = getResources();
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(r.getString(R.string.notification_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(r.getString(R.string.notification_title))
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setNumber(notificationCounter++)
                .build();

        Log.d(AppUtil.TAG, "notificationCount: " + notificationCounter);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(AppUtil.TAG, "GoogleApiClient onConnected - isConnected: " + mGoogleApiClient.isConnected());

        // Making connection and registering for location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(AppUtil.TAG, "GoogleApiClient onConnectionSuspended: " + mGoogleApiClient.isConnected());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(AppUtil.TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d(AppUtil.TAG, "Firing onLocationChanged");
        Log.d(AppUtil.TAG, "OfferTrackerService - onLocationChanged called at " + new Date());
        mCurrentLocation = location;

        if (mCurrentLocation != null && service != null) {
            // RESTful webservice call to fetch personalized offer details for the customer
            Log.d(AppUtil.TAG, "invoking PostDataToServer.execute method");
            new PostDataTOServer().execute();
        }

    }

    //09-21 07:18:24.501 15679-15679/com.sriram.rbsbankoffers W/System.err: android.os.NetworkOnMainThreadException
//    09-21 07:18:24.501 15679-15679/com.sriram.rbsbankoffers W/System.err:     at android.os.StrictMode$AndroidBlockGuardPolicy.onNetwork(StrictMode.java:1273)
    private class PostDataTOServer extends AsyncTask<Void, Void, Void> {

        String response = "";
        //Create HashMap Object to send parameters to web service
        HashMap<String, String> postDataParams;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if (mCurrentLocation != null) {
                String latitude = String.valueOf(mCurrentLocation.getLatitude());
                String longitude = String.valueOf(mCurrentLocation.getLongitude());

                Log.d(AppUtil.TAG, "Latitude @ webservice call : " + latitude);
                Log.d(AppUtil.TAG, "Longitude @ webservice call : " + longitude);

                postDataParams = new HashMap<>();
                postDataParams.put("latitude", latitude);
                postDataParams.put("longitude", longitude);

                //Call pullDataFromServer() method to call webservice and store result in response
                response = service.pullDataFromServer(AppUtil.REST_SERVICE_URL, postDataParams);
                try {
                    json = new JSONObject(response);
                    //Get Values from JSONobject
                    Log.d(AppUtil.TAG, "RESTful Service response: " + response);
                    Log.d(AppUtil.TAG, "RESTful Service json: " + json);

                    success = 1;
                } catch (JSONException e) {
                    e.printStackTrace();
                    success = 0;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.d(AppUtil.TAG, "PostDataToServer onPostExecute started: ");
            if (success == 1) {
                Log.d(AppUtil.TAG, "success: " + success);
                String text = text = "Lt: " + mCurrentLocation.getLatitude() + ", Lg: " + mCurrentLocation.getLongitude();
                showNotification(text + ", Json: " + json);
            }
        }
    }

}
