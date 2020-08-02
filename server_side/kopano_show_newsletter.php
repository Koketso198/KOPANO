<?php

$username = "s1908676";
$password = "Belinda3108";
$database = "d1908676";

$db = mysqli_connect("127.0.0.1", $username, $password, $database);

$query = mysqli_query ($db, "SELECT * FROM (((ACCEPTED_REQUEST_REVIEWS INNER JOIN ACCEPTED_REQUEST ON ACCEPTED_REQUEST_REVIEWS.ACCEPTED_REQUEST_ID = ACCEPTED_REQUEST.ACCEPTED_REQUEST_ID) INNER JOIN REQUEST ON ACCEPTED_REQUEST.REQUEST_ID = REQUEST.REQUEST_ID) INNER JOIN COMPANY ON ACCEPTED_REQUEST.COMPANY_REGISTRATION_NUMBER = COMPANY.COMPANY_REGISTRATION_NUMBER)");

$data = array();

while($row = $query -> fetch_assoc()){
  $data[] = $row;
}

echo json_encode($data);


?>


