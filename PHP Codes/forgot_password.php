<?php
require 'database.php';

$email = $_POST['email'];

$sql = "SELECT email,password FROM users WHERE email LIKE '$email'";

$result = mysqli_query($connection,$sql);
$response = array();

if(mysqli_num_rows($result)>0){
    $row = mysqli_fetch_assoc($result);
    
    mail($row["email"], "Password Request", "Your Account Password is :". $row["password"], "");
    echo "success";

}else {
    echo "failed";
}

mysqli_close($connection);
?>