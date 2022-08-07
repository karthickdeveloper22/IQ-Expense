<?php
require 'database.php';

if(isset($_POST['id'])){
    $id = $_POST['id'];
    
    $sql = $connection->prepare("SELECT expenseid,name,userid FROM expense_categories WHERE userid='$id'");

    $sql->execute();
    $sql->bind_result($expenseid,$name,$userid);

    $expense = array();

    while($sql->fetch()){
        $temp = array();
    
        $temp['expenseid'] = $expenseid;
        $temp['name'] = $name;
        $temp['userid'] = $userid;
    
        array_push($expense,$temp);
    }

    echo json_encode($expense);
}

?>