<?php

$username = "s1908676";
$password = "Belinda3108";
$database = "d1908676";

$db = mysqli_connect("127.0.0.1", $username, $password, $database);

$citizen_id = $_REQUEST["citizen_id"];

$query = mysqli_query ($db, "SELECT * FROM ((REQUEST INNER JOIN ACCEPTED_REQUEST ON REQUEST.REQUEST_ID = ACCEPTED_REQUEST.REQUEST_ID) INNER JOIN COMPANY ON ACCEPTED_REQUEST.COMPANY_REGISTRATION_NUMBER = COMPANY.COMPANY_REGISTRATION_NUMBER) WHERE REQUEST.CITIZEN_ID = '$citizen_id'");

$data = array();

while($row = $query -> fetch_assoc()){
  $data[] = $row;
}

echo json_encode($data);


?>
