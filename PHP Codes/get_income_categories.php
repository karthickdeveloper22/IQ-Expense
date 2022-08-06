<?php
require 'database.php';

if(isset($_POST['id'])){
    $id = $_POST['id'];
    
    $sql = $connection->prepare("SELECT incomeid,name,userid FROM income_categories WHERE userid='0' OR userid='$id'");

    $sql->execute();
    $sql->bind_result($incomeid,$name,$userid);

    $income = array();

    while($sql->fetch()){
        $temp = array();
    
        $temp['incomeid'] = $incomeid;
        $temp['name'] = $name;
        $temp['userid'] = $userid;
    
        array_push($income,$temp);
    }

    echo json_encode($income);
}

?>