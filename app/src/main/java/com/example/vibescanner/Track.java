package com.example.vibescanner;



import java.util.ArrayList;

public class Track extends Extension {

    private ArrayList<Trackpoint> trackPoints;
    int a;
    public Track() {

    }


    public ArrayList<Trackpoint> getTrackPoints() {
        return this.trackPoints;
    }

    public void setTrackPoints(ArrayList<Trackpoint> trkPts) {
        this.trackPoints = trkPts;
    }

}

