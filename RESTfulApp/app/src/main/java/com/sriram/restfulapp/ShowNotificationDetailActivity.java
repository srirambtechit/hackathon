package com.sriram.restfulapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class ShowNotificationDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notification_detail);

         Intent intent = getIntent();
        Log.d("RestDebug", "intent: " + intent);
        Log.d("RestDebug", "intent: " + intent.getDataString());
    }


}
