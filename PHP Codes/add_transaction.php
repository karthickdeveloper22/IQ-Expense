<?php
require 'database.php';

    $type = $_POST['type'];
    $category = $_POST['category'];
    $note = $_POST['note'];
    $amount = $_POST['amount'];
    $userid = $_POST['userid'];
    $timestamp = $_POST['timestamp'];

    $sql = "INSERT INTO transactions (type,category,note,amount,userid,timestamp) VALUES ('$type','$category','$note','$amount','$userid','$timestamp')";


    if($connection->query($sql) === TRUE){
        echo "success";
    }else {
        echo "failed";
    }


?>