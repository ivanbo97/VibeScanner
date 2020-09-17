package com.example.vibescanner;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class UploadingService extends Service {

    private String fileLocation;
    public static final String CHANNEL_ID = "UploadingServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Uploading Service")
                .setContentText("Gpx file is uploading!")
                .setTicker("Processing!")
                .setContentIntent(pendingIntent)
                .build();
        startForeground(10, notification);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        fileLocation = (String) intent.getExtras().get("filePath");
        Thread uploadingThread = new Thread(new FileUploadRunnable(fileLocation, UploadingService.this, getApplicationContext()));
        uploadingThread.start();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
