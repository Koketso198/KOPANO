<?php

$username = "s1908676";
$password = "Belinda3108";
$database = "d1908676";

$db = mysqli_connect("127.0.0.1", $username, $password, $database);

$company_registration_number = $_REQUEST["company_registration_number"];
$company_description = $_REQUEST["company_description"];
$company_name = $_REQUEST["company_name"];
$company_phone_number = $_REQUEST["company_phone_number"];
$company_email_address = $_REQUEST["company_email_address"];
$company_website = $_REQUEST["company_website"];
$company_number_of_employees = $_REQUEST["company_number_of_employees"];
$company_hiring_eligibility = $_REQUEST["company_hiring_eligibility"];
$company_password = $_REQUEST["company_password"];

$query = mysqli_query ($db, "insert into COMPANY values('$company_registration_number', '$company_description', '$company_name', '$company_phone_number', '$company_email_address', '$company_number_of_employees','$company_website', '$company_hiring_eligibility', sha1('$company_password'))");

if ($query){
  echo 1;
}
else {
  echo 0;
}

//test queries
//https://lamp.ms.wits.ac.za/home/s1908676/kopano_register_company.php?company_registration_number=123&company_description=abc&company_name=acs&company_phone_number=asd&company_email_address=eyg&company_website=dfg&company_number_of_employees=76&company_hiring_eligibility=1&company_password=hhf

//test query 2
//https://lamp.ms.wits.ac.za/home/s1908676/kopano_register_company.php?company_registration_number=543&company_description=mfh&company_name=hgd&company_phone_number=afg&company_email_address=pau&company_website=znj&company_number_of_employees=43&company_hiring_eligibility=0&company_password=wqg


?>
