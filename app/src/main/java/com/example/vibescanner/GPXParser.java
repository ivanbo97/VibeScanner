package com.example.vibescanner;


import android.util.Log;
import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class GPXParser {
    private ExtensionParser extensionParser ;
    private Logger logger;
    private static final String fileHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><gpx version=";

    //Not used in current app version!!!
    private XmlPullParser parser;

    public GPXParser() {

        extensionParser = new ExtensionParser();
        logger = Logger.getLogger(this.getClass().getName());
    }


    public void firstWriteGPX(GPX gpxObj, BufferedWriter gpxFileStream) throws IOException {

        gpxFileStream.write(fileHeader);
        this.addBasicGPXInfoToNode(gpxObj,gpxFileStream);
        if (gpxObj.getTrack() != null) {
            Track trkObj = gpxObj.getTrack();
            this.addTrackToGPXNode(trkObj, gpxFileStream);
        }
        else {
            Log.d("Error","No tracks were found !");
            gpxFileStream.write("</gpx>");
        }

    }

    public void intermediateWriteGPX(ArrayList <Trackpoint> trackPoints, BufferedWriter gpxFileStream) throws IOException{
        //Writing just after the </trkpt> closing tag of the previous element
        Iterator trackPointIter = trackPoints.iterator();
        while(trackPointIter.hasNext())
        {
            this.addGenericWaypointToGPXNode((Trackpoint)trackPointIter.next(),gpxFileStream);
        }
    }
    public void finalWriteGPX(BufferedWriter gpxFileStream)
    {
        try {
            gpxFileStream.write("</trkseg>");//closing <trkseg> element
            gpxFileStream.write("</trk>");//closing <trk> element
            gpxFileStream.write("</gpx>");//closing <gpx> element
        }catch (IOException e){
            e.printStackTrace();
            Log.d("Error","Final writing actions were corrupted!!!");
        }
    }

    private void addGenericWaypointToGPXNode( Trackpoint trackPointObj, BufferedWriter gpxFileStream) throws IOException {

            if (trackPointObj.getLatitude() != null &&trackPointObj.getLongitude() != null) {
                gpxFileStream.write("<trkpt lat=\""+trackPointObj.getLatitude().toString()+
                        "\" lon=\""+trackPointObj.getLongitude().toString()+"\"");
                if (trackPointObj.getExtensionsParsed() > 0) {

                    gpxFileStream.write(">");
                    gpxFileStream.write("<extensions>");
                    extensionParser.writeWaypointExtensionData(trackPointObj, gpxFileStream);

                }
                else
                    gpxFileStream.write("/>"); //No extension data put an end to the <trkpt> element
            }
    }

    private void addTrackToGPXNode(Track trackObj, BufferedWriter gpxFileStream) throws IOException {

        gpxFileStream.write("<trk><trkseg>");

        if (trackObj.getTrackPoints() != null) {
            Iterator trackpointsIterator = trackObj.getTrackPoints().iterator();

            while(trackpointsIterator.hasNext()) {
                this.addGenericWaypointToGPXNode((Trackpoint)trackpointsIterator.next(), gpxFileStream);
            }
        }
    }

    private void addBasicGPXInfoToNode(GPX gpxObj,BufferedWriter gpxFileStream) {


        if (gpxObj.getVersion() != null) {
            try {
                gpxFileStream.write("\""+gpxObj.getVersion()+"\" ");
            }catch (IOException e)
            {e.printStackTrace();
                Log.d("ERROR:","FAILED WRITING ATTRIBUTES");}

        }
        if (gpxObj.getCreator() != null) {
            try {
                gpxFileStream.write("creator= \""+gpxObj.getCreator()+"\">");
            }catch (IOException e)
            {e.printStackTrace();
             Log.d("ERROR:","FAILED WRITING ATTRIBUTES");
            }
        }
    }
}

