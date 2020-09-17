package com.example.vibescanner;


import java.util.LinkedHashMap;

public class Extension {

    protected LinkedHashMap<String, Float> extensionData;

    public Extension() {

    }

    public LinkedHashMap<String, Float> getExtensionData() {
        return this.extensionData;
    }

    public void setExtensionData(LinkedHashMap<String, Float> extData) {
        this.extensionData = extData;
    }

    public void addExtensionData(String element, Float value) {
        if (this.extensionData == null) {
            this.extensionData = new LinkedHashMap();
        }

        this.extensionData.put(element, value);
    }

    public Float getExtensionData(String element) {
        return this.extensionData != null ? this.extensionData.get(element) : null;
    }

    public int getExtensionsParsed() {
        return this.extensionData != null ? this.extensionData.size() : 0;
    }

    public LinkedHashMap<String, Float> getHashMap() {
        return extensionData;
    }
}


