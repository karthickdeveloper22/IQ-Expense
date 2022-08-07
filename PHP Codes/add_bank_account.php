<?php
require 'database.php';

if(isset($_POST['bank'])){
    $userid = $_POST['id'];
    $bank = $_POST['bank'];
    
    $sql = "INSERT INTO bank_accounts (bank_name,userid) VALUES ('$bank','$userid')";
    
    $result = mysqli_query($connection,$sql);
    
    if($result){
        echo "success";
    }else {
        echo "failed";
    }
}

?>