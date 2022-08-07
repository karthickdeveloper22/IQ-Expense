<?php
require 'database.php';

$id = $_POST['id'];
$subscribed = $_POST['subscribed'];

$sql = "UPDATE `users` SET `subscribed`='$subscribed' WHERE id='$id'";

if($connection->query($sql) === TRUE){
        echo "success";
    }else {
        echo "failed" . $connection->error;
    }

?>