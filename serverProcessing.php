<?php 
	$file_path = "gpxtracks/";
	$file_path = $file_path.basename($_FILES['uploaded_file']['name']);
	if(move_uploaded_file($_FILES['uploaded_file']['tmp_name'],$file_path))
		echo "success";
	else
    echo "error";
    
$db = new Mysqli('127.0.0.1:3306', 'root', '', 'tracks');
set_time_limit(1000);
$iterator = simplexml_load_file($file_path,"SimpleXMLIterator");
$creator = $iterator["creator"];
$db->query("INSERT INTO trk SET creator = '$creator'");
$lasttrkid = $db->insert_id;
$trkptiter = $iterator->trk->trkseg->children();
$hasOrientationInfo = false;
$hasAccelerationInfo = false;
foreach($trkptiter as $trkpt){	
	$lon = $trkpt["lon"];
	$lat = $trkpt["lat"];
	$db->query("INSERT INTO trackpt SET trackid='$lasttrkid', lat='$lat',lon='$lon'");
	$lasttrkptid = $db->insert_id;	
	if($trkpt->count()>0)
	{
	$extinfo = $trkpt->extensions->WptExt->children();
	$sqlAcceleration = array();
	$sqlOrientation = array();
	foreach ($extinfo as $extinf)
	{
		$extinfoname = $extinf->getName();
		if(strpos($extinfoname ,'xAcc')!==false){
			$hasAccelerationInfo = true;
			$xAccel = $extinf->__toString();			
			$strAccelVals = "('$lasttrkptid',".$xAccel.',';		   
		}
		else if(strpos($extinfoname ,'yAcc')!==false){
			$yAccel = $extinf->__toString();
			$strAccelVals=$strAccelVals.$yAccel.',';   
		}
		else if(strpos($extinfoname ,'zAcc')!==false){
			$zAccel = $extinf->__toString();		
			$strAccelVals=$strAccelVals.$zAccel.')';
			array_push($sqlAcceleration,$strAccelVals);
			}
		else if(strpos($extinfoname ,'xOr')!==false){
			$hasOrientationInfo = true;
			$xOrient = $extinf->__toString();
			$strOrientVals = "('$lasttrkptid',".$xOrient.',';
			}
		else if(strpos($extinfoname ,'yOr')!==false){
			$yOrient = $extinf->__toString();
			$strOrientVals = $strOrientVals.$yOrient.',';		   
		}
		else if(strpos($extinfoname ,'zOr')!==false){
			$zOrient = $extinf->__toString();
			$strOrientVals = $strOrientVals.$zOrient.')';
			array_push($sqlOrientation,$strOrientVals);	   
		}		
	}
	if($hasAccelerationInfo === true)
                         $db->query("INSERT INTO acceleration (trackptid,xAccel,yAccel,zAccel)VALUES".implode(',',$sqlAcceleration));

	if($hasOrientationInfo === true)
	    $db->query("INSERT INTO rotation (trackptid,xOrient,yOrient,zOrient)VALUES".implode(',',$sqlOrientation));
              }	
     $hasOrientationInfo = false;
    $hasAccelerationInfo = false;
}
unset($iterator);	
http_response_code(200);
 ?>
