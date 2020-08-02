<?php

$username = "s1908676";
$password = "Belinda3108";
$database = "d1908676";

$db = mysqli_connect("127.0.0.1", $username, $password, $database);

$request_id = $_REQUEST["request_id"];
$request_status = $_REQUEST["request_status"];

$query = mysqli_query ($db, "UPDATE REQUEST SET REQUEST_STATUS = '$request_status' WHERE REQUEST_ID = '$request_id'");

if($query){
  echo 1;
}
else {
  echo 0;
}

//https://lamp.ms.wits.ac.za/home/s1908676/kopano_request_status_update.php?request_id=1&request_status=1

?>
