package com.example.vibescanner;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapDrawer {

    private MapView map;
    private List<Overlay> overlayList;
    private IMapController mapController;
    private  boolean lastDraw;



    private static boolean firstWaypoint;

    Trackpoint currentWayPoint;

    float XAccelAvg = 0, YAccelAvg = 0, ZAccelAvg = 0;
    GeoPoint lastGeoPoint =null;


    public MapDrawer(MapView map) {
        this.map = map;
        firstWaypoint = true;
        overlayList = map.getOverlays();
        mapController = map.getController();
        lastDraw = false;
    }

    public  void setLastDraw (){
        lastDraw = true;
    }
    void drawOnMap(ArrayList<Trackpoint> trackpoints){

        Iterator<Trackpoint> trackPtsIterator = trackpoints.iterator();
        HashMap<String,Float> extensionData;
        Iterator <Map.Entry<String,Float>>  extensionIterator;
        float xAcc = 0, yAcc = 0, zAcc = 0;
        int extensionDataSize ;
        Polyline segment;

        while(trackPtsIterator.hasNext())
        {

            currentWayPoint = trackPtsIterator.next();
            extensionData = currentWayPoint.getExtensionData();
            GeoPoint currentGeoPoint = new GeoPoint(currentWayPoint.getLatitude(),currentWayPoint.getLongitude());
            if(extensionData!=null) {
                extensionIterator = extensionData.entrySet().iterator();
                extensionDataSize = extensionData.size();
                while (extensionIterator.hasNext()) {
                    Map.Entry<String, Float> pair = (Map.Entry<String, Float>) extensionIterator.next();
                    String key = pair.getKey();
                    if (key.contains("xAcc")) {

                        xAcc += pair.getValue();


                    } else if (key.contains("yAcc")) {

                        yAcc += pair.getValue();

                    } else if (key.contains("zAcc")) {
                        zAcc += pair.getValue();

                    }
                }

                XAccelAvg = xAcc / (float)extensionDataSize;
                YAccelAvg = yAcc / (float)extensionDataSize;
                ZAccelAvg = zAcc / (float)extensionDataSize;

                Log.d("XAccelAvg",String.valueOf(XAccelAvg));
                Log.d("YAccelAvg",String.valueOf(YAccelAvg));
                Log.d("ZAccelAvg",String.valueOf(ZAccelAvg));
            }
            else
            {
                XAccelAvg = 0;
                YAccelAvg = 0;
                ZAccelAvg = 0;

            }
            if(firstWaypoint)
            {
                lastGeoPoint = currentGeoPoint;
                GeoPoint startPoint = new GeoPoint(lastGeoPoint.getLatitude(),lastGeoPoint.getLongitude());
                mapController.setZoom(9.5);
                mapController.setCenter(startPoint);
                firstWaypoint = false;
                Marker newMarker = new Marker(map);
                newMarker.setPosition(startPoint);
                newMarker.setTitle("START POINT");
                newMarker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);
                overlayList.add(newMarker);
            }
            else
            {
                segment = new Polyline(map);
                segment.addPoint(lastGeoPoint);
                segment.addPoint(currentGeoPoint);


                if(XAccelAvg > 10 || YAccelAvg > 10 || ZAccelAvg > 10)
                {
                    segment.setColor(Color.RED);
                    Log.d("Info","RED");

                }

                else if(XAccelAvg < 7 && XAccelAvg > 5.5 || YAccelAvg < 7 && YAccelAvg > 5.5 || ZAccelAvg < 7 && ZAccelAvg > 5.5  )
                {
                    segment.setColor(Color.YELLOW);
                    Log.d("Info","YELLOW");

                }
                else
                {
                    segment.setColor(Color.GREEN);
                    Log.d("Info","GREEN");
                }

                segment.getPaint().setStrokeJoin(Paint.Join.ROUND);
                segment.getPaint().setStrokeCap(Paint.Cap.ROUND);
                // polylines.add(segment);
                overlayList.add(segment);
                lastGeoPoint = currentGeoPoint;

            }

        }
        if(lastDraw) {
            Marker lastMarker = new Marker(map);
            Trackpoint lastTrkpoint = trackpoints.get(trackpoints.size()-1);
            GeoPoint lastPoint = new GeoPoint(lastTrkpoint.getLatitude(),lastTrkpoint.getLongitude());
            lastMarker.setPosition(lastPoint);
            lastMarker.setTitle("END POINT");
            lastMarker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);
            overlayList.add(lastMarker);
        }
        GPXHandler.setTrackPointsDrawn(true);
    }
}
