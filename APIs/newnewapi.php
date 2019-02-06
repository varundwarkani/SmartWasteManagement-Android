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

if ($_GET["percent"]>80) {
  # alert municipality
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
        ),

        'service' => array
        (
            'Chennai' => array
            (
                'key' => array
              (
                rand(10,100) => array
               (
                'lat' => $_GET['lat'],
                'lon' => $_GET['lon'],
                'percentage' => $_GET['percent']
                )
              )
            )
        )
);
}
else
{
  if ($_GET["percent"]<0) {
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
                'percentage' => 0
              )
            )
        )
);
  }
  else
  {
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
  }
}

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