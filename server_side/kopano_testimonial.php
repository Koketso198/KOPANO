<?php

$username = "s1908676";
$password = "Belinda3108";
$database = "d1908676";

$db = mysqli_connect ("127.0.0.1", $username, $password, $database);

$accepted_request_id = $_REQUEST["accepted_request_id"];
$accepted_request_company_testimonial = $_REQUEST["accepted_request_company_testimonial"];
$accepted_request_project_pictures = $_REQUEST["accepted_request_project_pictures"];

$query = mysqli_query ($db, "UPDATE ACCEPTED_REQUEST SET ACCEPTED_REQUEST_COMPANY_TESTIMONIAL = '$accepted_request_company_testimonial', ACCEPTED_REQUEST_PROJECT_PICTURES = '$accepted_request_project_pictures' WHERE ACCEPTED_REQUEST_ID = '$accepted_request_id'");

if ($query){
  echo 1;
}
else {
  echo 0;
}


//https://lamp.ms.wits.ac.za/home/s1908676/kopano_testimonial.php?accepted_request_id=6&accepted_request_company_testimonial=ddd&accepted_request_project_pictures=ddd


?>
