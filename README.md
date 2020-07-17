# About VibeScanner
The purpose of this Android application is to register vibrations that occured during travelling with different vehicles. Data is collected from different device sensors: Linear Acceleration, Rotation Vector, GPS Reciever. After processesing, the collected data is stored in a gpx file. Thus you can see the mechanical influence imposed to your device during your travel. The generated track can be visualised in OpenStreetMaps and different segments of the route are colored in green, yellow, red, according to the different levels of vibration. You can also upload a file to remote server and distribute the data in different tables in relational database.

# A brief description of the application specific classes
  
# 1. class Extension 
    This class is a representation of the <extension> element which is part of the GPX 1.1 schema.
    The children of this element store values related to acceleration and rotation events that occured during the travel.
    The class has a member - extensionData which is a reference to an object of class LinkedHashMap <String, Float>. 
    String value is a key which corresponds to a tag name (for example: "<xAcc0>" - x-axis acceleration,"<xOri0>" - rotation around x-axis).
    In the object of class Float are stored values representing accelerations (m/s^2) or rotations around the coordinate axes.
    
# 2. class Trackpoint 
     This class inherits class Extension and provides implementation of <trkpt> element from GPX 1.1 schema. It is used for describing a
     single geographical point and additional information about the mechanical influence imposed on the device before reaching the next point.
    
