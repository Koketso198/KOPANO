<?php

$username = "s1908676";
$password = "Belinda3108";
$database = "d1908676";

$db = mysqli_connect("127.0.0.1", $username, $password, $database);

$citizen_id = $_REQUEST["citizen_id"];

$query = mysqli_query($db, " SELECT * FROM REQUEST WHERE CITIZEN_ID = '$citizen_id' ORDER BY REQUEST_POST_DATE DESC");

$data = array();

while($row = $query -> fetch_assoc()){
  $data[] = $row;
}

echo json_encode($data);

//https://lamp.ms.wits.ac.za/home/s1908676/kopano_show_citizen_requests.php?citizen_id=4

?>
