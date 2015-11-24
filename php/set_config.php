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

        $id_java = $_GET['id_java'];
        $config = $_GET['config'];
        
        $sql = "UPDATE `cerberus_db`.`users` SET `mode`='" . $config . "' WHERE id_rasp ='" . $id_java . "';";

        echo $sql_insert . "</br>";

        if ($conn->query($sql) === TRUE) {
            echo "New record created successfully";
        } else {
            echo "Error: " . $sql . "<br>" . $conn->error;
        }

        $conn->close();
?>
