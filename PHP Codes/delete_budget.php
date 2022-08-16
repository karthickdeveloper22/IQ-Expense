<?php
require 'database.php';

if(isset($_POST['budget_id']))

    $budget_id = $_POST['budget_id'];

    $sql = "DELETE FROM budgets WHERE budget_id='$budget_id'";


    if($connection->query($sql) === TRUE){
        echo "success";
    }else {
        echo "failed";
    }


?>