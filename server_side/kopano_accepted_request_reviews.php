<?php

$username = "s1908676";
$password = "Belinda3108";
$database = "d1908676";

$db = mysqli_connect("127.0.0.1", $username, $password, $database);

$accepted_request_id = $_REQUEST["accepted_request_id"];
$accepted_request_reviews_review = $_REQUEST["accepted_request_reviews_review"];
$accepted_request_reviews_date = $_REQUEST["accepted_request_reviews_date"];

$query = mysqli_query($db, "insert into ACCEPTED_REQUEST_REVIEWS values(0, '$accepted_request_id','$accepted_request_reviews_review',STR_TO_DATE('$accepted_request_reviews_date', '%d-%m-%Y'))");

if ($query){
  echo 1;
}
else {
  echo 0;
}

//https://lamp.ms.wits.ac.za/home/s1908676/kopano_accepted_request_reviews.php?accepted_request_id=6&accepted_request_reviews_review=qqq&accepted_request_reviews_date=02-09-1998

?>
