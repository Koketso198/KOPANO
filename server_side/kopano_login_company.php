<?php

$username = "s1908676";
$password = "Belinda3108";
$database = "d1908676";

$db = mysqli_connect("127.0.0.1", $username, $password, $database);

$company_phone_number = $_REQUEST["company_phone_number"];
$company_password = $_REQUEST["company_password"];

$query = mysqli_query($db, "SELECT COMPANY_REGISTRATION_NUMBER FROM COMPANY WHERE COMPANY_PHONE_NO = '$company_phone_number' AND COMPANY_PASSWORD = sha1('$company_password')");

if (mysqli_num_rows($query)){
  $row = $query -> fetch_assoc();
  echo $row["COMPANY_REGISTRATION_NUMBER"];
}
else {
  echo 0;
}


//https://lamp.ms.wits.ac.za/home/s1908676/kopano_login_company.php?company_phone_number=asd&company_password=hhf


?>
