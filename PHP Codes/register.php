<?php
require 'database.php';

$email = $_POST["email"];
$password = $_POST["password"];

$sql = "INSERT INTO users (email, password) VALUES ('$email', '$password')";

        if($connection->query($sql) === TRUE){
            echo "success";
        }else {
            echo "Email Already Registered!";
            //echo mysqli_error($connection);
        }

?>