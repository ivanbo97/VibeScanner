package com.example.vibescanner;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import xdroid.toaster.Toaster;

public class MapActivity extends AppCompatActivity {

    private MapView map = null;
    private Bundle bundle = null;
    private FileInputStream filePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        bundle = getIntent().getExtras();
        try {
            filePath = new FileInputStream(bundle.getString("filePath"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapInit();
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        MapDrawer mapDrawer = new MapDrawer(map);
        GPXHandler handler = new GPXHandler(mapDrawer);
        Toaster.toastLong("Generating map, please wait!!!");
        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            saxParser.parse(filePath, handler);
            filePath.close();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    void mapInit() {
        Context ctx = getApplicationContext();
        setContentView(R.layout.map_activity);
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
    }

}