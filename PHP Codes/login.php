<?php
require 'database.php';

$email = $_POST["email"];
$password = $_POST["password"];

$sql = "SELECT * FROM users WHERE email LIKE '$email' AND password LIKE '$password'";
$result = mysqli_query($connection, $sql);

$check = mysqli_num_rows($result);

if($check>0){
    echo "success";
}else{
    echo "Incorrect Email or Password!";
}
?>