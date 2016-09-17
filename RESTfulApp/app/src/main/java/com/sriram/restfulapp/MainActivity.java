package com.sriram.restfulapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class MainActivity extends AppCompatActivity {

    Button whereAmIButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        whereAmIButton = (Button) findViewById(R.id.button1);
        Log.d("RestDebug", "whereAmIButton: " + whereAmIButton);
    }

    public void navigateToLocationActivity(View view) {
        Intent intent = new Intent(this, LocationActivity.class);
        MainActivity.this.startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new HttpRequestTask().execute();
    }


    public void showNotification(String text) {
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, ShowNotificationDetailActivity.class), 0);

        Resources r = getResources();
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(r.getString(R.string.notification_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(r.getString(R.string.notification_title))
//                .setContentText(r.getString(R.string.notification_text))
                .setContentText(text)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, Greeting> {
        @Override
        protected Greeting doInBackground(Void... params) {
            try {
                final String url = "http://rest-service.guides.spring.io/greeting";
                RestTemplate restTemplate = new RestTemplate();
                Log.d("RestDebug", "restTemplate: " + restTemplate);
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Greeting greeting = restTemplate.getForObject(url, Greeting.class);
                Log.d("RestDebug", "success: " + greeting);
                return greeting;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Greeting greeting) {
            if (greeting == null) {
                Log.d("RestDebug", "greeting is null");
                return;
            }

            showNotification(greeting.getContent());

            Log.d("RestDebug", "id: " + greeting.getId());
            Log.d("RestDebug", "text: " + greeting.getContent());
        }

    }
}
