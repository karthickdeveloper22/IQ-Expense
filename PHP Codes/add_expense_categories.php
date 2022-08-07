<?php
require 'database.php';

if(isset($_POST['name'])){
    $userid = $_POST['id'];
    $name = $_POST['name'];
    
    $sql = "INSERT INTO expense_categories (name,userid) VALUES ('$name','$userid')";
    
    $result = mysqli_query($connection,$sql);
    
    if($result){
        echo "success";
    }else {
        echo "failed";
    }
}

?>