<?php
require 'database.php';

$id = $_POST['id'];
$title = $_POST['title'];
$description = $_POST['description'];
$amount = $_POST['amount'];
$spend = $_POST['spend'];
$timestamp = $_POST['timestamp'];
    
    $sql = "INSERT INTO budgets (title,description,amount,spend,userid,timestamp) VALUES ('$title','$description','$amount','$spend','$id','$timestamp')";
    
   /*$result = mysqli_query($connection,$sql);*/
    
    if($connection->query($sql) === TRUE){
    echo "success";
    }else {
    echo "failed";
    }
    
    
    /*if($result){
        echo "success";
    }else {
        echo "failed";
    }*/

?>