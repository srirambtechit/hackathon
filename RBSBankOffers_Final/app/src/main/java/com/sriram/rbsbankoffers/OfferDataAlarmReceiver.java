package com.sriram.rbsbankoffers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OfferDataAlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 1;

    public static final String ACTION = "com.sriram.rbsbankoffers.alarm";

    public OfferDataAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, OfferTrackerIntentService.class);
        i.putExtra("alarm", "RBSBankOffers Alarm");
        context.startService(i);
    }

}
