package com.example.vibescanner;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class GPXHandler extends DefaultHandler {

    private static int upperTransferLimit = 40;
    private static boolean trackPointsDrawn;
    private MapDrawer mapDrawer;
    private Trackpoint currentTrackPointObj;
    private LinkedHashMap<String, Float> currentExtensionData;
    private StringBuilder currentElementValue;
    private ArrayList<Trackpoint> trackPoints;
    private Trackpoint lastTrkPt;

    public GPXHandler(MapDrawer mapDrawer) {
        this.mapDrawer = mapDrawer;
        trackPoints = new ArrayList<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        if (qName.equalsIgnoreCase("trkpt")) {
            currentTrackPointObj = new Trackpoint();
            currentTrackPointObj.setLatitude(Double.parseDouble(attributes.getValue("lat")));
            currentTrackPointObj.setLongitude(Double.parseDouble(attributes.getValue("lon")));

            Log.d("LAT:", attributes.getValue("lat"));
            Log.d("LON", attributes.getValue("lon"));
        } else if (qName.equals("extensions")) {
            currentExtensionData = new LinkedHashMap();
        } else if (qName.contains("xAcc") || qName.contains("yAcc") || qName.contains("zAcc") || qName.contains("xOri") || qName.contains("yOri") || qName.contains("zOri"))
            currentElementValue = new StringBuilder();
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        currentElementValue.append(new String(ch, start, length));
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (qName.equals("gpx") || qName.equals("extensions") || qName.equals("trk"))
            return;
        else if (qName.equalsIgnoreCase("trkpt")) {
            trackPoints.add(currentTrackPointObj);
            int listSize = trackPoints.size();
            if (listSize >= upperTransferLimit) {

                lastTrkPt = trackPoints.get(listSize - 1);
                mapDrawer.drawOnMap(trackPoints);
                trackPoints.clear();

                trackPoints.add(lastTrkPt);
                trackPointsDrawn = false;
            }
        } else if (qName.equals("trkseg") && !trackPointsDrawn) {
            mapDrawer.setLastDraw();
            mapDrawer.drawOnMap(trackPoints);

        } else if (qName.equals("trkseg"))
            mapDrawer.setLastDraw();
        else if (qName.equals("WptExt"))
            currentTrackPointObj.setExtensionData(currentExtensionData);
        else {
            currentExtensionData.put(qName, Float.valueOf(currentElementValue.toString()));
            Log.d("TAG:", qName);
            Log.d("TAG VALUE", currentElementValue.toString());
        }
    }

    public static void setTrackPointsDrawn(boolean trackPointsDrawn) {
        GPXHandler.trackPointsDrawn = trackPointsDrawn;
    }
}
