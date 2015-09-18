<?php
	$con = mysql_connect("localhost", "root", "12345");
	mysql_select_db("cerberus_db", $con);
	$login_java = $_GET['login_java'];
	$result = mysql_query("SELECT id FROM login_id WHERE login =\"" . $login_java . "\"");
	$arr = array();

	while($record = mysql_fetch_array($result)){
		$id = $record['id'];
		$arr = array('id' => $id);
	}
	//create json array here
	echo json_encode($arr);
?>
