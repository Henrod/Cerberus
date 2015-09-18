<?php
	$con = mysql_connect("localhost", "root", "12345");
	mysql_select_db("cerberus_db", $con);
	$id_java = $_GET['id_java'];
	$result = mysql_query("SELECT * FROM users WHERE id = " . $id_java);
	$arr = array();

	while($record = mysql_fetch_array($result)){
		$lat = $record['lat'];
		$long = $record['long'];$id = $record['id'];
		$id = $record['id'];
		$moveu = $record['moveu'];
		$time = $record['time'];

		$arr = array('lat' => $lat, 'long' => $long, 'id' => $id, 'moveu' => $moveu, 'time' => $time);
	}
	//create json array here
	echo json_encode($arr);
?>
