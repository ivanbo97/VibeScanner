package com.example.vibescanner;


import java.util.Date;

public class Trackpoint extends Extension {
    private Double latitude;
    private Double longitude;
    private Double elevation;
    private Date time;
    private long timeOfHandling;

    public Double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(Double lat) {
        this.latitude = lat;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(Double lon) {
        this.longitude = lon;
    }

    public long getTimeOfHandling() {
        return timeOfHandling;
    }

    public void setTimeOfHandling(long timeOfHandling) {
        this.timeOfHandling = timeOfHandling;
    }
}
