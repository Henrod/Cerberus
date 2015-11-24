<?php
	$host = "localhost";
	$user = "root";
	$passwd = "12345";
	$database = "cerberus_db";

	$conn = new mysqli($host, $user, $passwd, $database);

	$id_rasp = $_GET['id_rasp'];

	$sql = "INSERT INTO `cerberus_db`.`users` (`lat`, `long`, `moveu`, `time`, `id_rasp`, `mode`) VALUES ('0', '0', 'nao', '20000', '" . $id_rasp  . "', 'Segurança total');";

	echo $sql_insert . "</br>";

	if ($conn->query($sql) === TRUE) {
	    echo "New record created successfully";
	} else {
	    echo "Error: " . $sql . "<br>" . $conn->error;
	}
	$conn->close();
?>
