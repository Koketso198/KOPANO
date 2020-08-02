<?php

$username = "s1908676";
$password = "Belinda3108";
$database = "d1908676";

$db = mysqli_connect("127.0.0.1", $username, $password, $database);

$request_id = $_REQUEST["request_id"];
$company_registration_no = $_REQUEST["company_registration_no"];
$accepted_request_acceptance_date = $_REQUEST["accepted_request_acceptance_date"];

$query = mysqli_query($db, "insert into ACCEPTED_REQUEST(REQUEST_ID, COMPANY_REGISTRATION_NUMBER, ACCEPTED_REQUEST_ACCEPTANCE_DATE) values('$request_id', '$company_registration_no', STR_TO_DATE('$accepted_request_acceptance_date', '%d-%m-%Y'))");

if ($query){
  echo 1;
}
else {
  echo 0;
}

//https://lamp.ms.wits.ac.za/home/s1908676/kopano_accepted_request.php?request_id=1&company_registration_no=123&accepted_request_acceptance_date=31-08-1999

?>
