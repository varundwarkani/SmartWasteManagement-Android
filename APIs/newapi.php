<?php
   if( $_GET["percent"] ) {

  // 	echo $_GET['lat'];
   	//echo $_GET['lon'];

   	// Constants
$FIREBASE = "https://smartwastemanage-66c48.firebaseio.com/";
$NODE_DELETE = "temperature.json";
$NODE_GET = "temperature.json";
$NODE_PATCH = ".json";
$NODE_PUT = "temperature.json";

$categories = array
(
        'garbage' => array
        (
            'Chennai' => array
            (
                'key' => array
            	(
                'lat' => $_GET['lat'],
                'lon' => $_GET['lon'],
                'percentage' => $_GET['percent']
            	)
            )
        )
);

$json = json_encode( $categories );
$curl = curl_init();
// Update
curl_setopt( $curl, CURLOPT_URL, $FIREBASE . $NODE_PATCH );
curl_setopt( $curl, CURLOPT_CUSTOMREQUEST, "PATCH" );
curl_setopt( $curl, CURLOPT_POSTFIELDS, $json );
curl_setopt( $curl, CURLOPT_RETURNTRANSFER, true );
$response = curl_exec( $curl );
curl_close( $curl );
echo $response . "\n";

      exit();
   }
?>