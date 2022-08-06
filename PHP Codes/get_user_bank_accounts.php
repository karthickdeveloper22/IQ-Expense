<?php
require 'database.php';

if(isset($_POST['id'])){
    $id = $_POST['id'];
    
    $sql = $connection->prepare("SELECT bank_id,bank_name,userid FROM bank_accounts WHERE userid='$id'");

    $sql->execute();
    $sql->bind_result($bank_id,$bank_name,$userid);

    $expense = array();

    while($sql->fetch()){
        $temp = array();
    
        $temp['bank_id'] = $bank_id;
        $temp['bank_name'] = $bank_name;
        $temp['userid'] = $userid;
    
        array_push($expense,$temp);
    }

    echo json_encode($expense);
}

?>