package com.example.vibescanner;

public class OrientationData {
    private Float xOrientation;
    private Float yOrientation;
    private Float zOrientation;
    private long handlingTime;
    public OrientationData(Float xOrientation, Float yOrientation, Float zOrientation, long handlingTime) {
        this.xOrientation = xOrientation;
        this.yOrientation = yOrientation;
        this.zOrientation = zOrientation;
        this.handlingTime = handlingTime;

    }
    public Float getxOrientation() {
        return xOrientation;
    }
    public Float getyOrientation() {
        return yOrientation;
    }
    public Float getzOrientation() {
        return zOrientation;
    }
    public long getHandlingTime() {
        return handlingTime;
    }
}
