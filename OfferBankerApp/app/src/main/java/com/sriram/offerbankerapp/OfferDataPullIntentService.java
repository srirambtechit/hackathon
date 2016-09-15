package com.sriram.offerbankerapp;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Date;


public class OfferDataPullIntentService extends IntentService {

    public OfferDataPullIntentService() {
        super("OfferDataPullIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // find out current position (latitude and longitude) of an user in Google Maps - GPS
        String userLocation = findUserLocation();

        // find out places available in current position of the user - Google Maps API
        String placeName = findMatchedPlace(userLocation);

        // web service call to get the list of offers for the selected place - Restful Service
        getOfferForPlace(placeName);

        // construction of data
        formAlertMessages();

        // Mechanism to send message to various notification systems, Toast, Notification bar, Activity
        sendToNotificationSystem();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            String message = bundle.getString("alarm") + " at " +  new Date();
            Log.i("Service", message);
            System.out.println("Message: " + message);
        }
    }


    private void formAlertMessages() {

    }

    private void sendToNotificationSystem() {

    }

    private void getOfferForPlace(String placeName) {
        // Send synchronous request
        // "http://localhost:8080/greeting?name=Prabhu"

    }

    private String findMatchedPlace(String userLocation) {
        return "";
    }

    private String findUserLocation() {
        return "";
    }

}
