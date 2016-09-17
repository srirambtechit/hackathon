package com.sriram.rbsbankoffers;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
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

import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class OfferTrackerIntentService extends Service {

    private GoogleApiClient mGoogleApiClient;
    private CustomerLocationProvider customerLocationProvider;

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

        // New thread spanned as location tracker service should not affect execution of UI thread
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

                // find out places available in current position of the user - Google Maps API
                String placeName = findMatchedPlace(userLocation);

                // web service call to get the list of offers for the selected place - Restful Service
                getOfferForPlace(placeName);

                // construction of data
                formAlertMessages();

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

    private void formAlertMessages() {

    }


    private void getOfferForPlace(String placeName) {
        // Send synchronous request
        // "http://localhost:8080/greeting?name=Prabhu"
    }

    private String findMatchedPlace(Location userLocation) {
        return "";
    }


    private void showNotification(String text) {
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, HomeActivity.class), 0);

        Resources r = getResources();
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(r.getString(R.string.notification_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(r.getString(R.string.notification_title))
                .setContentText(text)
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

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {

            if (!customerLocationProvider.isLocationAccessGranted()) {
                // get location access
            }

            location = customerLocationProvider.getCurrentLocation();
            if (location != null) {
                Log.d(AppUtil.TAG, "Location we got " + location.getLatitude() + ", " + location.getLongitude());
            }
        } else {
            Log.d(AppUtil.TAG, "GoogleApiClient is not connected");
        }
        return location;
    }

}
