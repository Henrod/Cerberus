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
	$id_rasp    = $_GET['id_java'];

	$result = mysql_query("SELECT login FROM login_id WHERE login =\"" . $login_java . "\""); 
	$login_exist = false;
	while($record = mysql_fetch_array($result)) {
		$login_exist = true;
	}

	$result = mysql_query("SELECT free_rasp FROM login_id WHERE id_rasp =\"" . $id_rasp . "\""); 
	$free_rasp = true;
	$exist_rasp = false;
	while($record = mysql_fetch_array($result)) {
		$exist_rasp = true;
		if ($record['free_rasp'] == 1){
			$free_rasp = false;
		}
	}

        if ($login_exist) { 
            	echo "Login existente"; 
        } else if (!$free_rasp) {
		echo "Dispositivo em uso";
	} else if (!$exist_rasp) {
		echo "ID inexistente";
	} else { 
		$sql = "UPDATE `cerberus_db`.`login_id` SET login='" . $login_java . "', passwd='" . $senha_java . "', free_rasp='1' WHERE id_rasp='" . $id_rasp . "'";

		echo $sql_insert . "</br>";

		if ($conn->query($sql) === TRUE) {
		    echo "New record created successfully";
		} else {
		    echo "Error: " . $sql . "<br>" . $conn->error;
		}

		$sql = "INSERT INTO `cerberus_db`.`users` (`lat`, `long`, `moveu`, `time`, `id_rasp`) VALUES ('0', '0', 'nao', '20000', '" . $id_rasp . "');";

		echo $sql_insert . "</br>";

		if ($conn->query($sql) === TRUE) {
		    echo "New record created successfully";
		} else {
		    echo "OPAAA Error: " . $sql . "<br>" . $conn->error;
		}
	}
	$conn->close();
?>
