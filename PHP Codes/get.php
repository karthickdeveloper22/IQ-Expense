<?php
require "database.php";

/*if(isset($_POST['email'])){
    $email = $_POST['email'];

$sql = "SELECT * FROM users WHERE email='$email'";

$result = mysqli_query($connection, $sql);

while ($row = $result->fetch_assoc()) {
echo $row['id'];
    }
}*/

//Arrays
if(isset($_POST['email'])){
    $email = $_POST['email'];
    
    $sql = $connection->prepare("SELECT id,subscribed FROM users WHERE email='$email'");

$sql->execute();
$sql->bind_result($id,$subscribed);

$details = array();

while($sql->fetch()){
    $temp = array();
    
    $temp['id'] = $id;
    $temp['subscribed'] = $subscribed;
    
    array_push($details,$temp);
}

echo json_encode($details);
}


?>