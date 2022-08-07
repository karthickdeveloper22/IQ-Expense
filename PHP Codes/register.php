<?php

require 'database.php';

$email = $_POST["email"];
$password = $_POST["password"];

$sql = "INSERT INTO users (email, password) VALUES ('$email', '$password')";

    if($connection->query($sql) === TRUE){
        echo "success";
    }else {
        echo "failed";
    }

/*$check_email = "SELECT * FROM users WHERE email LIKE '$email'";

if(mysqli_num_rows($check_email) > 0){
    echo "Already Registered!";
}else{
    
}*/

?>