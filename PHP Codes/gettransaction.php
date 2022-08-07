<?php
require 'database.php';

if(isset($_POST['id'])){
    $id = $_POST['id'];
    
    $sql = $connection->prepare("SELECT trans_id,type,category,note,amount,userid,timestamp FROM transactions WHERE userid='$id'");

$sql->execute();
$sql->bind_result($trans_id,$type,$category,$note,$amount,$userid,$timestamp);

$transaction = array();

while($sql->fetch()){
    $temp = array();
    
    $temp['trans_id'] = $trans_id;
    $temp['type'] = $type;
    $temp['category'] = $category;
    $temp['note'] = $note;
    $temp['amount'] = $amount;
    $temp['userid'] = $userid;
    $temp['timestamp'] = $timestamp;
    
    array_push($transaction,$temp);
}

echo json_encode($transaction);
}


?>