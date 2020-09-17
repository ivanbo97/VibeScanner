package com.example.vibescanner;


public class GPX extends Extension {
    private String version;
    private String creator;
    private Track track;

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String var1) {
        this.version = var1;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String var1) {
        this.creator = var1;
    }

    public Track getTrack() {
        return this.track;
    }

    public void setTrack(Track trk) {
        this.track = trk;
    }

    public void addTrack(Track trk) {
        this.track = trk;
    }
}

