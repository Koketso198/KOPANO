<?php

$username = "s1908676";
$password = "Belinda3108";
$database = "d1908676";

$db = mysqli_connect("127.0.0.1", $username, $password, $database);

$citizen_name = $_REQUEST["citizen_name"];
$citizen_surname = $_REQUEST["citizen_surname"];
$citizen_preffered_communication_language = $_REQUEST["citizen_preffered_communication_language"];
$citizen_phone_number = $_REQUEST["citizen_phone_number"];
$citizen_email_address = $_REQUEST["citizen_email_address"];
$citizen_community_role = $_REQUEST["citizen_community_role"];
$citizen_password = $_REQUEST["citizen_password"];

$query = mysqli_query ($db, "insert into CITIZEN values(0, '$citizen_name', '$citizen_surname', '$citizen_preffered_communication_language', '$citizen_phone_number','$citizen_email_address', '$citizen_community_role', sha1('$citizen_password'))");

if ($query){
  echo 1;
}
else {
  echo 0;
}

//test queries
//https://lamp.ms.wits.ac.za/home/s1908676/kopano_register_citizen.php?citizen_name=yst&citizen_surname=tdk&citizen_preffered_communication_language=fof&citizen_phone_number=tdf&citizen_email_address=eyg&citizen_community_role=mds&citizen_password=tsk

//test query 2
//https://lamp.ms.wits.ac.za/home/s1908676/kopano_register_citizen.php?citizen_name=rqe&citizen_surname=isd&citizen_preffered_communication_language=uaj&citizen_phone_number=b&citizen_email_address=ufh&citizen_community_role=uwi&citizen_password=b

//https://lamp.ms.wits.ac.za/home/s1908676/kopano_register_citizen.php?citizen_name=a&citizen_surname=a&citizen_preffered_communication_language=a&citizen_phone_number=a&citizen_email_address=a&citizen_community_role=a&citizen_password=a

?>
