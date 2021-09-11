# About VibeScanner
The purpose of this Android application is to register vibrations that occured during travelling with different vehicles. Data is collected from different device sensors: Linear Acceleration, Rotation Vector, GPS Reciever. After processesing, the collected data is stored in a gpx file. Thus you can see the mechanical influence imposed to your device during your travel. Generated track can be visualised in OpenStreetMaps and different segments of the route are colored in green, yellow, red, according to the different levels of vibration. You can also upload gpx files to a server and distribute the data in different tables in a relational database.
 **Important notice: Internet connection is not required for recording your track and mechanical influences.**
*Source code of project's classes: https://github.com/ivanbo97/VibeScanner/tree/master/app/src/main/java/com/example/vibescanner

*Server-side processing script (PHP): https://github.com/ivanbo97/VibeScanner/tree/master/server_side

### An example of recorded track

![TrackExample](https://i.ibb.co/CvzbJVB/Screenshot-20210911-084232-Vibe-Scanner1-53.jpg)

# A brief description of application specific classes
 
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
evaluates the difference in rotation angle compared to the previous event. Methods of this class are also executed by a seperate thread. Thus rotation
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

This class encapsulates functionality needed for structring the collected data from sensors in XML format. Taking into consideartion the idea that the content of the app memory will be periodically transfered into a gpx file, we can conclude that there would be 3 different types of file writing: primary, intermediate and final.

**Primary writing (void onLocationChanged(Location location) )** : Standart initialization of XML document with XML version and charcter encoding. Then the root element with its attributes is added. Example file content after the first writing opeartion:
 
 ?xml version="1.0" encoding="UTF-8"?>
 <gpx
  version="1.1"
  creator="Anonymous" 
 
 ***Intermediate writing :*** When the size of the ArrayList<Trackpoint> trackpoints reaches 4, we should transfer the data from RAM to file and free the memory allocated for those objects. During the intemediate write new trkpt elements with additional information are added to trkseg element.
 
 ***Final writing (void finalWriteGPX(BufferedWriter gpxFileStream)):*** Final writing is about closing the elements which have been initialized by the primary writing.
 
 # 12. class ExtensionParser
 
 This class encapsulates functionality related to structuring the extension information coming with a location point - acceleartion data/orientation data, and writing it in a gpx file. Extension data is stored in a LinkedHasMap <String, Float>. The key is the name of the tag (example: "xAcc0","yAcc0"...) and in the object of class Float is stored the tag's content. After iterating through each pair of the LinkedHashMap, closing tags for the corresponding elements are added.
 
 # Parsing the data stored in a gpx file
 
 The information stored in a gpx file, related to device location, outer mechanical influence and orientation in space, should be extracted as values and later used for providing different functonality. In the current application this data will be used for representing a track in OpenStreetMap. Reading from the gpx file should be step by step in order to prevent running out of memory. Therefore it is important to choose a proper API for parsing. In our case this is SAX (Simple API for XML).
 
 SAX provides event-based model for accessing XML documents and the main events are: start of element, end of element, sequence of characters. After encountering one of these events, the API invokes the corresponding callback methods which are encapsulated in class GPXHandler. 

# 13. class GPXHandler

Description of the implementation of the callback methods:
 - void startElement(String uri, String localName, String qName, Attributes attributes) : The application has different behaviour depending on current tag's name stored in qName. If trkpt is encounterd during parsing of the gpx file, a new object of class Trackpoint is created and its members - latitude and longitude are being set related to the content of the Attributes object passed to the method. If the current element is <extensions>, this means that we have reached the additional data and have to provide a place for storing it - object of class LinkedHashMap. If the tag of the encountered element starts with xAcc, yAcc, zAcc or xOri, yOri, zOri we should allocate memory for storing the content of this tag - in object of class StringBuilder.
 
 - public void characters(char ch[], int start, int length) : This method is called by the API when sequence of charcters between starting tag and closing tag is being encountered.
 
- public void endElement(String uri, String localName, String qName) : The implementation of this method is very important for manging the memory used by the application. It controls the current number of Trackpoint objects. If closing tag : </trkpt> is encountered , this means that all the information about the location is being read and the reference to the Trackpoint object can be added to ArrayList <Trackpoint> trackpoints. After that, if the current number of points in memory exceeds the uppper limit, it is time for map drawing which is performed by methods of class MapDrawer.
 It is important to notice the role of trackPointsDrawn flag in this situation: When the API reaches /trkseg closing tag but there are still track points in the ArrayList which have not been visualsied on the map yet because their number is below the upper limit of drawing. 
 
 # 14. class MapDrawer
 
 This class encapsulates functionality related to iterating through Trackpoint object, extracting information and drawing on OpenStreetMaps. The color of the segment between two locations is defined by the average value of acceleration between these two points.
 
 # Uploading gpx files to server
 
 The process of uploading is carried out by a foreground service which starts a seperate thread. Thus the main functionality and performance of the application is not affected. 
 # 15. class UploadingService
  
  When this type of service is started, location of the file that is going to be uploaded is passed from the MainActivity via an Intent.
  
 # 16. class FileUploadRunnable
 
 The work that is going to be performed by the thread is implemented in method void run(). The creation and sending of the HTTP request is done by using OKHttp library. After the file has been transfered via POST method, the php script (save_file.php) is executed on server-side. It processes the file and records the data in corresponding tables of a relation database.
  
