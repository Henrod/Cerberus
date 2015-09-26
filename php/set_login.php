<?php
	#$con = mysql_connect("localhost", "root", "12345");
	#mysql_select_db("cerberus_db", $con);
	$conn = new mysqli("localhost", "root", "12345", "cerberus_db");
	if ($conn->connect_error) {
	    die("Connection failed: " . $conn->connect_error);
	}



	$login_java = $_GET['login_java'];
	$senha_java = $_GET['senha_java'];
	$sql = "INSERT INTO login_id (login, passwd) VALUES ('" . $login_java . "', '" . $senha_java . "')";

#	$sql_insert = "INSERT INTO login_id (login, passwd) VALUES ('" . $login_java . "', '" . $senha_java . "');";
	echo $sql_insert . "</br>";

	if ($conn->query($sql) === TRUE) {
	    echo "New record created successfully";
	} else {
	    echo "Error: " . $sql . "<br>" . $conn->error;
	}

	$conn->close();
#	$mysql_con = mysqli_query($con, $sql_insert);
#	$error = mysqli_error($mysql_con);
	
#	echo $erro;
#	mysqli_close($con);
	
	 //$result = mysql_query($statement);

	//while($record = mysql_fetch_array($result)){
	//}
?>
