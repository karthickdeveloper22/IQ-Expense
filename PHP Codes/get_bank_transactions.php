<?php
require 'database.php';

if(isset($_POST['bank_id'])){
    $bank_id = $_POST['bank_id'];
    $userid = $_POST['userid'];
    
    $sql = $connection->prepare("SELECT bank_trans_id,category,type,note,amount,bank_id,userid,timestamp FROM bank_transactions WHERE bank_id=$bank_id");

$sql->execute();
$sql->bind_result($bank_trans_id,$category,$type,$note,$amount,$bank_id,$userid,$timestamp);

$transaction = array();

while($sql->fetch()){
    $temp = array();
    
    $temp['bank_trans_id'] = $bank_trans_id;
    $temp['category'] = $category;
    $temp['type'] = $type;
    $temp['note'] = $note;
    $temp['amount'] = $amount;
    $temp['bank_id'] = $bank_id;
    $temp['userid'] = $userid;
    $temp['timestamp'] = $timestamp;
    
    array_push($transaction,$temp);
}

echo json_encode($transaction);
}


?>