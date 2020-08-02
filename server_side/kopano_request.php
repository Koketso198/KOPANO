<?php

$username = "s1908676";
$password = "Belinda3108";
$database = "d1908676";

$db = mysqli_connect("127.0.0.1", $username, $password, $database);

$citizen_id = $_REQUEST["citizen_id"];
$request_location = $_REQUEST["request_location"];
$request_description = $_REQUEST["request_description"];
$request_status = $_REQUEST["request_status"];
$request_post_date = $_REQUEST["request_post_date"];

$query = mysqli_query($db, "insert into REQUEST values(0, '$citizen_id' ,'$request_location', '$request_description', '$request_status ', STR_TO_DATE('$request_post_date', '%d-%m-%Y')) ");

if ($query){
  echo 1;
}
else {
  echo 0;
}

//https://lamp.ms.wits.ac.za/home/s1908676/kopano_request.php?citizen_id=4&request_location=aaa&request_description=sss&request_status=bbb&request_post_date=31-08-1999

?>


