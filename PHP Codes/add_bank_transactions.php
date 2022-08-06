<?php
require 'database.php';

    $type = $_POST['type'];
    $category = $_POST['category'];
    $note = $_POST['note'];
    $amount = $_POST['amount'];
    $userid = $_POST['userid'];
    $bank_id = $_POST['bank_id'];
    $timestamp = $_POST['timestamp'];

    $sql = "INSERT INTO bank_transactions (type,category,note,bank_id,amount,userid,timestamp) VALUES ('$type','$category','$note','$bank_id','$amount','$userid','$timestamp')";


    if($connection->query($sql) === TRUE){
        echo "success";
    }else {
        echo "failed";
    }


?>