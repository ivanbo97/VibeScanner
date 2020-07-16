package com.example.vibescanner;

import android.util.Log;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


public class ExtensionParser  {


   public LinkedHashMap <String, Float> parseWaypointExtension(Node extensionNode) {


        LinkedHashMap<String, Float> extensionsInfo = new LinkedHashMap<String, Float>();
        Node wptExtensionNode;
        NodeList wptExtensionNodeChildren;
        Node wptExtChildElement;
        Log.d("Start the transfer from file to RAM:",String.valueOf(System.currentTimeMillis()));
        wptExtensionNode = extensionNode.getChildNodes().item(0);
        /*!!!!https://stackoverflow.com/questions/9869507/how-to-create-an-xml-using-xmlserializer-in-android-app!!!!!

        * https://thedeveloperworldisyours.com/android/write-xml-in-android/*/
        Log.d("Num_of_recordings",String.valueOf(wptExtensionNode.getChildNodes().getLength()));
        int size = wptExtensionNode.getChildNodes().getLength();
        wptExtensionNodeChildren = wptExtensionNode.getChildNodes();
        for (int j = 0; j < size; j++)
        {
            wptExtChildElement = wptExtensionNodeChildren.item(j);
            extensionsInfo.put(wptExtChildElement.getNodeName(),Float.valueOf(wptExtChildElement.getFirstChild().getNodeValue()));

        }



       Log.d("End the transfer from file to RAM:",String.valueOf(System.currentTimeMillis()));


    return extensionsInfo;
    }

    //Not in used in current app version!!!
    public LinkedHashMap <String, Float> parseTrackPointExtension(XmlPullParser parser) throws XmlPullParserException,IOException {

        //currently we are on <trkpt> element, so we should move to <WptExt>
        parser.next();
        parser.next();
        parser.require(XmlPullParser.START_TAG,"","WptExt");
        LinkedHashMap<String, Float> extensionsInfo = new LinkedHashMap<String, Float>();

       // Log.d("Start the transfer from file to RAM:",String.valueOf(System.currentTimeMillis()));

        parser.next(); //moving to the first element representing extension data
        String tagName;
        int eventType = parser.getEventType();
        Float value;
        while(eventType!=XmlPullParser.END_DOCUMENT)
        {

            switch (eventType) {

                case XmlPullParser.START_TAG:
                    tagName = parser.getName();
                    if(tagName.equals("trkpt") || tagName.equals("trkseg") ) {
                       // Log.d("Parsing INFO","Reached the next track point or track segment!!!");
                        return extensionsInfo;
                    }
                    else {
                       // Log.d("Parsing INFO","Parsing extension DATA");
                        parser.next(); //go to the element content
                        value = Float.valueOf(parser.getText());
                      // Log.d("Extnesion Val",parser.getText());
                        extensionsInfo.put(tagName,value);
                    }
                break;

            }
            parser.next();
            eventType= parser.getEventType();
        }



       // Log.d("End the transfer from file to RAM:",String.valueOf(System.currentTimeMillis()));


           return null; //!!!!!!!!!!!!!!
    }



    public void writeWaypointExtensionData( Trackpoint trkpt, BufferedWriter gpxFileStream) {

        try {
            gpxFileStream.write("<WptExt>");

            Iterator<Map.Entry<String, Float>> iterator = trkpt.getHashMap().entrySet().iterator();

            Map.Entry<String, Float> pair;
            String key;
            while (iterator.hasNext()) {
                pair = (Map.Entry<String, Float>) iterator.next();
                key = pair.getKey();
                gpxFileStream.write("<"+key+">"+String.valueOf(pair.getValue())+"</"+key+">");
            }

            gpxFileStream.write("</WptExt></extensions></trkpt>");

        } catch (IOException e) {
        e.printStackTrace();
        Log.d("Error","Could not write extensions element");
          }
    }

}