package com.example.vibescanner;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.util.ArrayList;

public class AccelerationListener implements SensorEventListener {

    private ArrayList<AccelData> accelInfo;
    private long eventTime;
    boolean firstFixPassed;


    public AccelerationListener() {
        accelInfo = new ArrayList<>();
        firstFixPassed = false;
    }

    public ArrayList<AccelData> getAccelInfo() {
        return accelInfo;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        eventTime = System.currentTimeMillis();
        //Save data only when the first location has been found
        if (firstFixPassed) {

            //Re-zeroing if the measurement is not significant.
            if ((event.values[0] < 2 && event.values[0] > 0) || (event.values[0] > -2 && event.values[0] < 0))
                event.values[0] = 0;
            if ((event.values[1] < 2 && event.values[1] > 0) || (event.values[1] > -2 && event.values[1] < 0))
                event.values[1] = 0;
            if ((event.values[2] < 2 && event.values[2] > 0) || (event.values[2] > -2 && event.values[2] < 0))
                event.values[2] = 0;

            if (event.values[0] != 0 && event.values[1] != 0 && event.values[2] != 0) {
                accelInfo.add(new AccelData(event.values[0], event.values[1], event.values[2], eventTime));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public long getEventTime() {
        return eventTime;
    }
 public void newEvents () { accelInfo = new ArrayList<>(); }
    public void setFirstFixPassed(boolean firstFixPassed) {
        this.firstFixPassed = firstFixPassed;
    }
}
