<?php

	$host = "localhost";
	$user = "root";
	$passwd = "12345";
	$database = "cerberus_db";

	$conn = new mysqli($host, $user, $passwd, $database);

	if ($conn->connect_error) {
	    die("Connection failed: " . $conn->connect_error);
	}

	$con = mysql_connect($host, $user, $passwd);
	mysql_select_db($database, $con);
	
	$login_java = $_GET['login_java'];
	$senha_java = $_GET['senha_java'];

	$result = mysql_query("SELECT login FROM login_id WHERE login =\"" . $login_java . "\""); 
	$login_exist = false;
	while($record = mysql_fetch_array($result)) {
		$login_exist = true;
	}

        if ($login_exist) { 
            echo "Login já existente"; 
        } else { 
		$sql = "INSERT INTO login_id (login, passwd) VALUES ('" . $login_java . "', '" . $senha_java . "')";

		echo $sql_insert . "</br>";

		if ($conn->query($sql) === TRUE) {
		    echo "New record created successfully";
		} else {
		    echo "Error: " . $sql . "<br>" . $conn->error;
		}

		$sql = "INSERT INTO `cerberus_db`.`users` (`lat`, `long`, `moveu`, `time`) VALUES ('0', '0', 'nao', '20000');";

		echo $sql_insert . "</br>";

		if ($conn->query($sql) === TRUE) {
		    echo "New record created successfully";
		} else {
		    echo "Error: " . $sql . "<br>" . $conn->error;
		}
	}
	$conn->close();
?>
