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

        //Log for debugging purpose
        Log.d("Start the transfer from file to RAM:",String.valueOf(System.currentTimeMillis()));

        wptExtensionNode = extensionNode.getChildNodes().item(0);

        Log.d("Num_of_recordings",String.valueOf(wptExtensionNode.getChildNodes().getLength()));
        int size = wptExtensionNode.getChildNodes().getLength();
        wptExtensionNodeChildren = wptExtensionNode.getChildNodes();
        for (int j = 0; j < size; j++)
        {
            wptExtChildElement = wptExtensionNodeChildren.item(j);
            extensionsInfo.put(wptExtChildElement.getNodeName(),Float.valueOf(wptExtChildElement.getFirstChild().getNodeValue()));

        }


       //Log for debugging purpose

       Log.d("End the transfer from file to RAM:",String.valueOf(System.currentTimeMillis()));


    return extensionsInfo;
    }

    //Not in used in current app version!!!


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