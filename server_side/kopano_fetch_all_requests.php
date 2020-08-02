<?php

$username = "s1908676";
$password = "Belinda3108";
$database = "d1908676";

$db = mysqli_connect("127.0.0.1", $username, $password, $database);

$query = mysqli_query($db, "SELECT * FROM REQUEST INNER JOIN CITIZEN ON CITIZEN.CITIZEN_ID = REQUEST.CITIZEN_ID");

$data = array();

while($row = $query -> fetch_assoc()){
  $data[] = $row;
}

echo json_encode($data);

//https://lamp.ms.wits.ac.za/home/s1908676/kopano_fetch_all_requests.php

?>
