package com.example.vibescanner;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class AccelAndRotRunnable implements Runnable {

    private Trackpoint currentWaypoint;
    private Trackpoint previousWaypoint;
    private ArrayList<Trackpoint> trkPts;
    private long eventTimestamp;
    private ArrayList <AccelData> accelData;
    private ArrayList <OrientationData> orientationData;

    private int timePassed;
    private float distancePassed;
    private int timeIntervalInSegment;

    private static int accRecordNum;

    public AccelAndRotRunnable( Trackpoint currentWaypoint, Trackpoint previousWaypoint, ArrayList<Trackpoint> trkPts, long locationEventHandlingTime, ArrayList<AccelData> accelData, ArrayList<OrientationData> orientationData,float distancePassed) {

        this.currentWaypoint = currentWaypoint;
        this.trkPts = trkPts;
        this.eventTimestamp = locationEventHandlingTime;
        this.accelData = accelData;
        this.orientationData = orientationData;
         this.distancePassed = distancePassed;
        this.previousWaypoint = previousWaypoint;
        timeIntervalInSegment = 0;
        accRecordNum = 0;
    }

    @Override
    public void run()  {

        if(distancePassed > 5)
        {
            timePassed = (int) (currentWaypoint.getTimeOfHandling() - previousWaypoint.getTimeOfHandling());
            
            //Distance between two geographical points is bigger than 5 m;
            timeIntervalInSegment =  (int)((float)(5*timePassed) / distancePassed);
            Log.d("TIME BETWEEN TWO POINTS",String.valueOf(timePassed));
            detailedAvgAccCalc();
            accRecordNum = 0; //Number of acceleration events

        } else
            avgAccCalc();



        orientationDataReading();

        trkPts.add(currentWaypoint);
        if(trkPts.size()>4) {

            try {
                if (ForegroundService.isFirstWriteInFile()) {
                    ForegroundService.getGpxParser().firstWriteGPX(ForegroundService.getGpxElement(), ForegroundService.getWriter());
                    ForegroundService.setFirstWriteInFileTofalse();
                } else
                    ForegroundService.getGpxParser().intermediateWriteGPX(trkPts, ForegroundService.getWriter());
                trkPts.clear();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

       CustomLocationListener.setPreviousTrackPoint(currentWaypoint);
    }

    public void detailedAvgAccCalc()
    {
        Log.d("CALC DATA","CALCULATING DETAILED AVG ACC");
        long lowerTime = previousWaypoint.getTimeOfHandling();
        long upperTime = currentWaypoint.getTimeOfHandling();
        long upperLimit = lowerTime + timeIntervalInSegment;

        Log.d("UPPER TIME",String.valueOf(upperTime));
        Log.d("UPPER LIMIT",String.valueOf(upperLimit));
        Log.d("LOWER TIME",String.valueOf(lowerTime));
        Log.d("TIME SLICE",String.valueOf(timeIntervalInSegment));

        Iterator<AccelData> accelDataIter = accelData.iterator();
        AccelData currentAccelData;
        float xAvgAccel = 0,yAvgAccel = 0,zAvgAccel = 0;
        float xAccel = 0,yAccel = 0,zAccel = 0;
        int cnt=0;
        int sizeAccelData = accelData.size();
        long handlingTime;

       for(int i = 0; i<sizeAccelData;i++)
       {
           currentAccelData = accelData.get(i);
           handlingTime = currentAccelData.getHandlingTime();
           Log.d("ACCEL EVENT TIME",String.valueOf(handlingTime));
           if(handlingTime>=lowerTime && handlingTime <= upperLimit  )
           {
               Log.d("CALC DATA","CALCULATING DETAILED AVG ACC");
               xAccel += currentAccelData.getxAxisAccel();
               yAccel += currentAccelData.getyAxisAccel();
               zAccel += currentAccelData.getzAxisAccel();
               cnt++;

           }
           else if (handlingTime>upperLimit && handlingTime<=upperTime)
           {

               xAvgAccel = xAccel / (float) cnt;
               yAvgAccel = yAccel / (float) cnt;
               zAvgAccel = zAccel / (float) cnt;
               cnt = 1;
               xAccel = currentAccelData.getxAxisAccel();
               yAccel = currentAccelData.getxAxisAccel();
               zAccel = currentAccelData.getxAxisAccel();
               upperLimit += timeIntervalInSegment; //Moving to the next time segment
               if(!Float.isNaN(xAvgAccel) && !Float.isNaN(yAvgAccel) && !Float.isNaN(zAvgAccel) ) {
                   addAccelDataToTrkPt(xAvgAccel, yAvgAccel, zAvgAccel);
                   accRecordNum++;
               }
           }
       }
    }


    public void avgAccCalc()
    {
        float xAvgAccel = 0,yAvgAccel = 0,zAvgAccel = 0;
        float xAccel = 0,yAccel = 0,zAccel = 0;
        int accelDataSize = accelData.size();
        Log.d("CALC DATA","CALCULATING AVG ACC");
        if(accelDataSize == 0 ) {
            Log.d("ACCEL DATA SIZE","00000");
            return;
        }
        else {
            Log.d("CALC DATA","CALCULATING AVG ACC");
            for (int i = 0; i < accelDataSize; i++) {
                AccelData currentAccelInstance = accelData.get(i);
                xAccel += currentAccelInstance.getxAxisAccel();
                yAccel += currentAccelInstance.getyAxisAccel();
                xAccel += currentAccelInstance.getzAxisAccel();

            }


            xAvgAccel = xAccel / (float) accelDataSize;
            yAvgAccel = yAccel / (float) accelDataSize;
            zAvgAccel = zAccel / (float) accelDataSize;

            if(!Float.isNaN(xAvgAccel)&& !Float.isNaN(yAvgAccel) && !Float.isNaN(zAvgAccel)) {
                currentWaypoint.addExtensionData("xAcc0", xAvgAccel);
                currentWaypoint.addExtensionData("yAcc0", yAvgAccel);
                currentWaypoint.addExtensionData("zAcc0", zAvgAccel);

            }
        }
    }
    public void addAccelDataToTrkPt (float xAvgAcc, float yAvgAcc, float zAvgAcc)
    {
            currentWaypoint.addExtensionData("xAcc" + String.valueOf(accRecordNum), xAvgAcc);
            currentWaypoint.addExtensionData("yAcc" + String.valueOf(accRecordNum), yAvgAcc);
            currentWaypoint.addExtensionData("zAcc" + String.valueOf(accRecordNum), zAvgAcc);
    }

    public void orientationDataReading ()
    {
        int orientationDataSize = orientationData.size();

        for (int i = 0;i < orientationDataSize;i++)
        {
            OrientationData currentOrientationInstance = orientationData.get(i);
            currentWaypoint.addExtensionData("xOri" + String.valueOf(i),currentOrientationInstance.getxOrientation());
            currentWaypoint.addExtensionData("yOri" + String.valueOf(i),currentOrientationInstance.getyOrientation());
            currentWaypoint.addExtensionData("zOri" + String.valueOf(i),currentOrientationInstance.getzOrientation());
        }
    }
}
