<?php
require 'database.php';

if(isset($_POST['trans_id']))

    $trans_id = $_POST['trans_id'];

    $sql = "DELETE FROM transactions WHERE trans_id='$trans_id'";


    if($connection->query($sql) === TRUE){
        echo "success";
    }else {
        echo "failed";
    }


?>