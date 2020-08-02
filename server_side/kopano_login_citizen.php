<?php

$username = "s1908676";
$password = "Belinda3108";
$database = "d1908676";

$db = mysqli_connect("127.0.0.1", $username, $password, $database);

$citizen_phone_number = $_REQUEST["citizen_phone_number"];
$citizen_password = $_REQUEST["citizen_password"];

$query = mysqli_query($db, "SELECT CITIZEN_ID FROM CITIZEN WHERE CITIZEN_PHONE_NO = '$citizen_phone_number' AND CITIZEN_PASSWORD = sha1('$citizen_password')");

if (mysqli_num_rows($query)){
  $row = $query -> fetch_assoc();
  echo $row["CITIZEN_ID"];
}
else {
  echo 0;
}


//https://lamp.ms.wits.ac.za/home/s1908676/kopano_login_citizen.php?citizen_phone_number=a&citizen_password=a

?>
