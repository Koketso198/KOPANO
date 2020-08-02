<?php

$username = "s1908676";
$password = "Belinda3108";
$database = "d1908676";

$db = mysqli_connect("127.0.0.1", $username, $password, $database);

$company_registration_number = $_REQUEST["company_registration_number"];

$query = mysqli_query ($db, "SELECT * FROM ((REQUEST INNER JOIN ACCEPTED_REQUEST ON REQUEST.REQUEST_ID = ACCEPTED_REQUEST.REQUEST_ID) INNER JOIN CITIZEN ON REQUEST.CITIZEN_ID = CITIZEN.CITIZEN_ID) WHERE ACCEPTED_REQUEST.COMPANY_REGISTRATION_NUMBER = '$company_registration_number'");

$data = array();

while($row = $query -> fetch_assoc()){
  $data[] = $row;
}

echo json_encode($data);


?>
