package com.example.vibescanner;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;

public class ForegroundService extends Service {


    private static GPXParser gpxParser;
    private static GPX gpxElement;
    private Track newTrack;
    private ArrayList<Trackpoint> trackPoints;

    private AccelerationListener accelerationListener;
    private RotationListener rotationListener;
    private CustomLocationListener locationListener;

    public static final String CHANNEL_ID = "ForeignServiceChannel";

    private SensorManager accelRotManager;
    private LocationManager locationManager;

    private Sensor accelerationSensor;
    private Sensor rotationSensor;

    private HandlerThread accelerationThread;
    private HandlerThread rotationThread;
    private HandlerThread locationThread;

    private static boolean firstWriteInFile;
    private static FileWriter fileWriter;
    private static BufferedWriter bufferedWriter;


    @Override
    public void onCreate() {
        super.onCreate();
        firstWriteInFile = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            fileWriter = new FileWriter(MainActivity.getFile());
            bufferedWriter = new BufferedWriter(fileWriter);
        } catch (Exception e) {
            e.printStackTrace();
            return START_NOT_STICKY;
        }

        gpxParser = new GPXParser();
        gpxElement = new GPX();
        gpxElement.setVersion("1.1");
        gpxElement.setCreator("Ivan Boy");
        newTrack = new Track();
        accelerationListener = new AccelerationListener();
        rotationListener = new RotationListener();
        trackPoints = new ArrayList<>();

        newTrack.setTrackPoints(trackPoints);
        gpxElement.addTrack(newTrack);

        accelerationListener = new AccelerationListener();
        locationListener = new CustomLocationListener(accelerationListener, rotationListener, trackPoints);

        accelRotManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        accelerationSensor = accelRotManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        rotationSensor = accelRotManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        accelerationThread = new HandlerThread("AccelerationThread"); //Thread with message queue
        locationThread = new HandlerThread("LocationThread");
        rotationThread = new HandlerThread("RotationThread");
        accelerationThread.start();

        Handler accelerationHandler = new Handler(accelerationThread.getLooper());
        accelRotManager.registerListener(accelerationListener, accelerationSensor, SENSOR_DELAY_NORMAL, accelerationHandler);
        rotationThread.start();
        Handler rotationHandler = new Handler(rotationThread.getLooper());
        accelRotManager.registerListener(rotationListener, rotationSensor, 10000000, rotationHandler);
        locationThread.start();
        Handler locationHandler = new Handler(locationThread.getLooper());
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, locationListener, locationHandler.getLooper());
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("FOREIGN SERVICE")
                .setContentText("Executing")
                .setTicker("Track is recording..")
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        accelRotManager.unregisterListener(accelerationListener);
        try {
            if (firstWriteInFile)
                gpxParser.firstWriteGPX(gpxElement, bufferedWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        gpxParser.finalWriteGPX(bufferedWriter);
        try {
            bufferedWriter.close();
            fileWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        locationManager.removeUpdates(locationListener);
        accelRotManager.unregisterListener(accelerationListener);
        accelRotManager.unregisterListener(rotationListener);
        accelerationThread.quitSafely();
        rotationThread.quitSafely();
        locationThread.quitSafely();
        super.onDestroy();
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

    public static GPXParser getGpxParser() {
        return gpxParser;
    }

    public static GPX getGpxElement() {
        return gpxElement;
    }

    public static BufferedWriter getWriter() {
        return bufferedWriter;
    }

    ;

    public static boolean isFirstWriteInFile() {
        return firstWriteInFile;
    }

    public static void setFirstWriteInFileTofalse() {
        firstWriteInFile = false;
    }
}


