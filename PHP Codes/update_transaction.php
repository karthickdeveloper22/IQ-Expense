<?php
require 'database.php';

    $trans_id = $_POST['trans_id'];
    $type = $_POST['type'];
    $category = $_POST['category'];
    $note = $_POST['note'];
    $amount = $_POST['amount'];
    $timestamp = $_POST['timestamp'];
    
    /*$query = "UPDATE `transactions` SET `type`='$type',`category`='$category',`note`='$note',`amount`='$amount',`timestamp`='$timestamp' WHERE trans_id='$trans_id'";*/

    $sql = "UPDATE `transactions` SET `type`='$type',`category`='$category',`note`='$note',`amount`='$amount',`timestamp`='$timestamp' WHERE trans_id='$trans_id'";


    if($connection->query($sql) === TRUE){
        echo "success";
    }else {
        echo "failed" . $connection->error;
    }

?>