package com.sriram.rbsbankoffers;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
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
public class OfferTrackerIntentService extends Service {

    // Place holders for location tracker
    private GoogleApiClient mGoogleApiClient;
    private CustomerLocationProvider customerLocationProvider;

    // Place holders for Webservice
    private JSONObject json;
    private int success = 0;
    private HTTPURLConnection service;

    // Place holders for notification system
    private static int notificationCounter = 1;
    private int notificationId = 0;

    public OfferTrackerIntentService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(AppUtil.TAG, "onCreate");

        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            Log.d(AppUtil.TAG, "GooglePlayService is not available");
            return;
        }

        // New thread spawned as location tracker service should not affect execution of UI thread
        new Thread(new Runnable() {
            @Override
            public void run() {

                mGoogleApiClient = new GoogleApiClient.Builder(OfferTrackerIntentService.this)
                        .addApi(LocationServices.API)
                        .build();

                customerLocationProvider = new CustomerLocationProvider(getApplicationContext(), mGoogleApiClient);

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
        int i = super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            String message = bundle.getString("alarm") + " at " + new Date();
            Log.d(AppUtil.TAG, message);


            Location userLocation = findUserLocation();

            if (userLocation != null) {
                // RESTful webservice call to fetch personalized offer details for the customer
                if (service != null) {
                    new PostDataTOServer().execute();
                }

                // Mechanism to send message to various notification systems, Toast, Notification bar, Activity
                String text = "you are at latitude of " + userLocation.getLatitude() + " and longitude of " + userLocation.getLongitude();
                showNotification(text);

            }

        }
        return i;
    }


    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
//            GooglePlayServicesUtil.getErrorDialog(status, HomeActivity.class, 0).show();
            return false;
        }
    }

    private void showNotification(String text) {
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, HomeActivity.class), 0);

        Resources r = getResources();
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(r.getString(R.string.notification_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(r.getString(R.string.notification_title))
                .setContentText(json + text)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setNumber(notificationCounter++)
                .build();

        Log.d(AppUtil.TAG, "notificationId: " + notificationId + ", notificationCount: " + notificationCounter);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // For displaying last latest 5 notification
        if (notificationId % 5 == 0) {
            notificationId = 0;
        }
//        notificationManager.notify(notificationId++, notification);
        notificationManager.notify(0, notification);

    }

    // Capturing current location of user using FusedLocationProvider in GoogleApiClient
    private Location findUserLocation() {
        Location location = null;
        Log.d(AppUtil.TAG, "findUserLocation 0");
        Log.d(AppUtil.TAG, "findUserLocation googClient : " + mGoogleApiClient);
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Log.d(AppUtil.TAG, "findUserLocation 1");

            Log.d(AppUtil.TAG, "findUserLocation 2");
            location = customerLocationProvider.getCurrentLocation();
            Log.d(AppUtil.TAG, "findUserLocation 3 : " + location);
            if (location != null) {
                Log.d(AppUtil.TAG, "Location we got " + location.getLatitude() + ", " + location.getLongitude());
            }
        } else {
            Log.d(AppUtil.TAG, "findUserLocation GoogleApiClient is not connected");
        }
        return location;
    }

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
            Location userLocation = findUserLocation();

            String latitude = String.valueOf(userLocation.getLatitude());
            String longitude = String.valueOf(userLocation.getLongitude());

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
                Log.d(AppUtil.TAG, "RESTful Service response: " + json);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (success == 1) {
            }
        }
    }

}
