package com.sriram.offerbankerapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DataAlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 12345;

    public static final String ACTION = "com.sriram.offerbankerapp.alarm";

    public DataAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, OfferDataPullIntentService.class);
        i.putExtra("alarm", "OfferBankerApp Alarm");
        context.startService(i);
    }
}
