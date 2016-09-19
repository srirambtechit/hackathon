package com.sriram.rbsbankoffers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.sriram.rbsbankoffers.util.AppUtil;

public class HomeActivity extends AppCompatActivity {

    private static final int APP_ALARM_INTERVAL_TIME_IN_MINUTES = 10000; // approx 1 minute

    private TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // It is a background process to fetch offer details from the server via RESTful Service call
        startOfferTrackerService();

        textView2 = (TextView) findViewById(R.id.textView2);

        Log.d(AppUtil.TAG, "HomeActivity onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(AppUtil.TAG, "HomeActivity onStart called");
        // Requesting to enable Location Service for RBSBankOffer application
        askPermission();

        Intent intent = getIntent();
        if (intent != null) {
            String offerDetail = intent.getStringExtra(AppUtil.NOTIFICATION_FULL_TEXT_INTENT_KEY);
            Log.d(AppUtil.TAG, "Full text : " + offerDetail);
            textView2.setText(offerDetail);
        }
    }

    private void askPermission() {
        Log.d(AppUtil.TAG, "HomeActivity askPermission entered");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                //If the user has denied the permission previously your code will come to this block
                //Here you can explain why you need this permission
                //Explain here why you need this permission
                Log.d(AppUtil.TAG, "HomeActivity askPermission confirmation and requesting permission");

                //ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_ACCESS_PERMISSION);

            } else {
                Log.d(AppUtil.TAG, "HomeActivity askPermission requesting permission");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, AppUtil.MY_LOCATION_ACCESS_PERMISSION);
            }
        }
        Log.d(AppUtil.TAG, "HomeActivity askPermission leaved");
    }

    // Setup a recurring alarm every half hour
    public void startOfferTrackerService() {
        // Construct an intent that will execute the OfferDataAlarmReceiver
        Intent intent = new Intent(getApplicationContext(), OfferDataAlarmReceiver.class);

        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, OfferDataAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Setup periodic alarm every 5 seconds
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                APP_ALARM_INTERVAL_TIME_IN_MINUTES, pIntent);
    }
}
