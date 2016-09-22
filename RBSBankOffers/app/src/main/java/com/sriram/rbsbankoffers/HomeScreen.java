package com.sriram.rbsbankoffers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.sriram.rbsbankoffers.util.AppUtil;

public class HomeScreen extends AppCompatActivity {

    private static final int APP_ALARM_INTERVAL_TIME_IN_MINUTES = 10000; // approx 1 minute

//    private TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home);
        setContentView(R.layout.copy_activity_home);

        // To display RBS logo in Application title bar
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.drawable.rbs_ic_titlebar);
            actionBar.setDisplayUseLogoEnabled(true);
        }

        // It is a background process to fetch offer details from the server via RESTful Service call
//        startOfferTrackerService();
        Intent offerDataIntent = new Intent(getApplicationContext(), OfferTrackerService.class);
        startService(offerDataIntent);

//        textView2 = (TextView) findViewById(R.id.textView2);

        Log.d(AppUtil.TAG, "HomeScreen onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(AppUtil.TAG, "HomeScreen onStart called");
        // Requesting to enable Location Service for RBSBankOffer application
        askPermission();

        Intent intent = getIntent();
        if (intent != null) {
            String offerDetail = intent.getStringExtra(AppUtil.NOTIFICATION_FULL_TEXT_INTENT_KEY);
            Log.d(AppUtil.TAG, "Full text : " + offerDetail);
//            textView2.setText(offerDetail);
        }
    }

    private void askPermission() {
        Log.d(AppUtil.TAG, "HomeScreen askPermission entered");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                //If the user has denied the permission previously your code will come to this block
                //Here you can explain why you need this permission
                //Explain here why you need this permission
                Log.d(AppUtil.TAG, "HomeScreen askPermission confirmation and requesting permission");

                //ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_ACCESS_PERMISSION);

            } else {
                Log.d(AppUtil.TAG, "HomeScreen askPermission requesting permission");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, AppUtil.MY_LOCATION_ACCESS_PERMISSION);
            }
        }
        Log.d(AppUtil.TAG, "HomeScreen askPermission leaved");
    }

}
