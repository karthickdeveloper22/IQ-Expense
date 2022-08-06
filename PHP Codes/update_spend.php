<?php
require 'database.php';

$budget_id = $_POST['budget_id'];
$spend = $_POST['spend'];

$sql = "UPDATE `budgets` SET `spend`='$spend' WHERE budget_id='$budget_id'";

if($connection->query($sql) === TRUE){
        echo "success";
    }else {
        echo "failed" . $connection->error;
    }

?>