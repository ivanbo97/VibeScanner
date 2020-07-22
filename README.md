# About VibeScanner
The purpose of this Android application is to register vibrations that occured during travelling with different vehicles. Data is collected from different device sensors: Linear Acceleration, Rotation Vector, GPS Reciever. After processesing, the collected data is stored in a gpx file. Thus you can see the mechanical influence imposed to your device during your travel. The generated track can be visualised in OpenStreetMaps and different segments of the route are colored in green, yellow, red, according to the different levels of vibration. You can also upload gpx files to a server and distribute the data in different tables in a relational database.

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

# 3. class Track
     This class corresponds to the <trk> element from GPX 1.1 schema and stores the geographic points of the route. It has a memeber - ArrayList of Trackpoints.
     Thus the order in which different locations occured is preserved because it is important for afterwards visualisation of the track.
     
# 4. class GPX
      
    This class has memebers String version, String creator which correspond to the <gpx> element attributes. In the current app version a single <gpx> element
    can have only one <trk> child element.
    
# 5. class AccelData

 The main purpose of this class is to encapsulate values registred by accelerometer during a single sensor event. The time of event handling
 is also stored because it is of importance when we start processing accumulated data. (further explanation about that can be found in the description
 of class AccelAndRotRunnable).

# 6. class OrientationData

It encapsulates data about the device orientation in space and the time of event handling.

# 7. class AccelerationListener

This class provides implementation of the callback methods which handle events registered by the device accelerometer. The main purpose is to obtain event 
values, filter out insignificant data and temporary store values in objects of class AccelData. It is important to mention that there is a sperate thread 
which executes the methods of this class.

# 8. class RotationListener

It encapsulates ArrayList of references to objects of class OrientationData and implements callback methods for handling events registered by Rotation Vector
Sensor. The values obtained by this synthetic sensor are elements from rotation vector which correspond to sin(θ/2)  (θ – rotation angle). The direct 
visualisation of this values can not give clarity about the device orientation in space. We have to convert the values to rotation matrix and after that
obtain the orientation which represents three values - rotation around x,y and z-axis. Not every single rotation event is stored. There is a filter which
evaluates the difference in rotation angle compared to the previous event. The methods of this class are also executed by a seperate thread. Thus rotation
events can be handled without missing acceleration and location events.

# 9. class AccelAndRotRunnable

This class provides functionality related to processing of acceleration data accumulated between two locations and also initialises file writing opeartions.
We should take into consideration the fact that the distance between two points is not fixed and can vary from 10m to 100m or more depending on the strength
of the GPS signal. If we directly calculate the arithmetic mean of the registered acceleration event values for greater distances, we will miss information about how the acceleration develops in time. The better approach would be to seperate this distance into small segments of the same size and calculate the arithmetic mean of acceleration in every single segment. After this processing, the accumulated information can be formated and stored in a gpx file. This whole work is done by a sperate thread, which makes the operations related to registering data from sensors, sepearte from data processing. 

# 10. class CustomLocationLitener

This class encapsulates the imlementation of the callback method - void onLocationChanged(Location location) which is invoked when location change event appears. 
Firstly, we create an instance of class Trackpoint and set its values for latitude and longitude depending on the content of the location object which is passed
to the method. If it is the first location, we should notify AccelerationListener and RotationListener objects ,by raising a flag, in order to start collecting data from their sensors. After that the thread for processing acceleration and rotation data is started. It makes its calculations using the data stored in accelInfo object
which is a member of the AccelerationListener object. Suppose there are acceleration events during the time of processing, we should guarantee that they are saved and
correspond to the next segment. So we have to create new instances of AccelData and OrientationData classes. Thus the rotation and acceleration listeners will
operate on different objects in memory than those of the processing thread.

# 11. class GPXParser

This class encapsulates functionality needed for structring the collected data from sensors in XML format. Taking into consideartion the idea that the content of the app memory will be periodically transfered into a gpx file, we can conclude that there would be 3 different tpypes of file writing: primary, intermediate and final.

***Primary writing (void onLocationChanged(Location location) )** : Standart initialization of XML document with XML version and charcter encoding. Then the root element with its attributes is added. Example file content after the first writing opeartion:
 
 ?xml version="1.0" encoding="UTF-8"?>
 <gpx
  version="1.1"
  creator="Anonymous" 
  
  



*!!!Expect soon update with description of the classes related to parsing gpx files, track drawing in OpenStreetMaps and uploading files to a server!!!* 
