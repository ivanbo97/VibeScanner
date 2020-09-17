package com.example.vibescanner;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

public class RotationListener implements SensorEventListener {

    private ArrayList<OrientationData> orientationInfo;

    private long eventTime;
    private float[] rotationMatrix;
    private float[] rotationVector;

    private float lastXRotation;
    private float lastYRotation;
    private float lastZRotation;

    private static boolean firstGPSFix;
    private static boolean firstMeasurement;


    public RotationListener() {
        orientationInfo = new ArrayList<OrientationData>();
        rotationMatrix = new float[16];
        rotationVector = new float[3];
        lastXRotation = 0;
        lastYRotation = 0;
        lastZRotation = 0;
        firstGPSFix = false;
        firstMeasurement = true;

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        eventTime = System.currentTimeMillis();
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
        SensorManager.getOrientation(rotationMatrix, rotationVector);

         /*Check if the change of orientation is significant and record it. If it is a first time measurement,
           record the information no matter what values of orientation are !!! */

        rotationVector[0] = (float) Math.toDegrees(rotationVector[0]);
        rotationVector[1] = (float) Math.toDegrees(rotationVector[1]);
        rotationVector[2] = (float) Math.toDegrees(rotationVector[2]);

        if (firstGPSFix) {
            if (firstMeasurement) {
                lastXRotation = rotationVector[0];
                lastYRotation = rotationVector[1];
                lastZRotation = rotationVector[2];
                firstMeasurement = false;
            } else if (Math.abs(rotationVector[0] - lastXRotation) > 50 || Math.abs(rotationVector[1] - lastYRotation) > 50 || Math.abs(rotationVector[2] - lastZRotation) > 50)
                orientationInfo.add(new OrientationData(rotationVector[0], rotationVector[1], rotationVector[2], eventTime));
            lastXRotation = rotationVector[0];
            lastYRotation = rotationVector[1];
            lastZRotation = rotationVector[2];
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

        //Intentionally left blank for future app development.
    }

    public ArrayList<OrientationData> getOrientationInfo() {
        return orientationInfo;
    }

    public void newEvents() {
        orientationInfo = new ArrayList<>();
    }

    public static void setFirstGPSFix(boolean firstGPSFix) {
        RotationListener.firstGPSFix = firstGPSFix;
    }
}
