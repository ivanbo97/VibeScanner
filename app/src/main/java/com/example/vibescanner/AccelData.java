package com.example.vibescanner;

public class AccelData {
   private Float xAxisAccel;
   private Float yAxisAccel;
   private Float zAxisAccel;
   private long handlingTime;

    public AccelData(Float xAxisAccel, Float yAxisAccel, Float zAxisAccel, long handlingTime ) {
        this.xAxisAccel = xAxisAccel;
        this.yAxisAccel = yAxisAccel;
        this.zAxisAccel = zAxisAccel;
        this.handlingTime = handlingTime;
    }

    public Float getxAxisAccel() {
        return xAxisAccel;
    }

    public Float getyAxisAccel() {
        return yAxisAccel;
    }

    public Float getzAxisAccel() {
        return zAxisAccel;
    }

    public long getHandlingTime() {return handlingTime;}
}
