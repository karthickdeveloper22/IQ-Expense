<?php
require 'database.php';

if(isset($_POST['id'])){
    $id = $_POST['id'];

$sql = "SELECT SUM(amount) FROM transactions WHERE type='Income' AND userid='$id'";

$result = mysqli_query($connection,$sql);

$row = mysqli_fetch_assoc($result);

echo $row['SUM(amount)'];
}
?>