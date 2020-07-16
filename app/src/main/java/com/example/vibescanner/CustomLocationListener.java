package com.example.vibescanner;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import xdroid.toaster.Toaster;

public class CustomLocationListener implements LocationListener {

    private AccelerationListener accelerationListener;
    private RotationListener rotationListener;
    private Trackpoint currentTrackPoint;
    private static Trackpoint previousTrackPoint;
    private ArrayList <Trackpoint> trkPts;
    private long locationHandlingTime;
    private Thread extensionWriterTh;
    private boolean firstFixPassed;
    private Location lastLocation;
    private float distancePassed;
    private int timePassed;

    public CustomLocationListener(AccelerationListener accelListener, RotationListener rotationListener, ArrayList<Trackpoint> trackPoints) {
        accelerationListener = accelListener;
        trkPts = trackPoints;
        this.rotationListener = rotationListener;
        firstFixPassed = false;
        lastLocation = null;
        previousTrackPoint = null;
        distancePassed = 0;
        timePassed = 0;
    }

    @Override
    public void onLocationChanged(Location location) {

        locationHandlingTime = System.currentTimeMillis();
        if(!firstFixPassed) {
            //Start registering acceleration and rotation data after the first location.
            firstTrkPt(location);
            return;
        }
        else {
            Toaster.toastLong("New location has been registered!!!");

            currentTrackPoint = new Trackpoint();
            currentTrackPoint.setLatitude(location.getLatitude());
            currentTrackPoint.setLongitude(location.getLongitude());
            currentTrackPoint.setTimeOfHandling(locationHandlingTime);
            Log.d("Location event:", String.valueOf(currentTrackPoint.getTimeOfHandling()) + " Sensor Event: " + String.valueOf(accelerationListener.getEventTime()));
            Log.d("Accuracy in meters:", String.valueOf(location.getAccuracy()));


            distancePassed = lastLocation.distanceTo(location);
            timePassed = (int) (currentTrackPoint.getTimeOfHandling() - previousTrackPoint.getTimeOfHandling());

            extensionWriterTh = new Thread(new AccelAndRotRunnable(currentTrackPoint, previousTrackPoint, trkPts, locationHandlingTime, accelerationListener.getAccelInfo(),rotationListener.getOrientationInfo(), distancePassed));
            extensionWriterTh.start();

            /*Creating new instances of AccelData and OrientationData classes,
              thus the old objects can be garbage collected after the execution of extensions writer thread .
             */
            accelerationListener.newEvents();
            rotationListener.newEvents();
            lastLocation = location;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {

        //Intentionally left blank for future app development!
    }

    @Override
    public void onProviderDisabled(String provider) {
        //Intentionally left blank for future app development!
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //Intentionally left blank for future app development!
    }

    public void firstTrkPt(Location location)
    {

        firstFixPassed = true;
        accelerationListener.setFirstFixPassed(firstFixPassed);
        RotationListener.setFirstGPSFix(true);
        currentTrackPoint = new Trackpoint();
        currentTrackPoint.setLatitude(location.getLatitude());
        currentTrackPoint.setLongitude(location.getLongitude());
        currentTrackPoint.setTimeOfHandling(locationHandlingTime);
        lastLocation = location;
        previousTrackPoint = currentTrackPoint;
        trkPts.add(currentTrackPoint);
        Toaster.toastLong("First location has beeen registered !!!");
        Log.d("FIRST POINT","ADDED");
        Log.d("Location event:",String.valueOf(currentTrackPoint.getTimeOfHandling()));
    }
    public static void setPreviousTrackPoint (Trackpoint trackPoint)
    {
        previousTrackPoint = trackPoint;
    }
}
