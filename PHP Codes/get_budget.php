<?php
require 'database.php';

if(isset($_POST['id'])){
    $id = $_POST['id'];
    
    $sql = $connection->prepare("SELECT budget_id,title,description,amount,spend,userid,timestamp FROM budgets WHERE userid='$id'");

$sql->execute();
$sql->bind_result($budget_id,$title,$description,$amount,$spend,$userid,$timestamp);

$budget = array();

while($sql->fetch()){
    $temp = array();
    
    $temp['budget_id'] = $budget_id;
    $temp['title'] = $title;
    $temp['description'] = $description;
    $temp['amount'] = $amount;
    $temp['spend'] = $spend;
    $temp['userid'] = $userid;
    $temp['timestamp'] = $timestamp;
    
    array_push($budget,$temp);
}

echo json_encode($budget);
}

?>