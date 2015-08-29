<?php
	$con = mysql_connect("localhost", "root", "12345");
	mysql_select_db("cerberus_db", $con);
	$result = mysql_query("SELECT * FROM users WHERE id = 2");
	$arr = array();

	while($record = mysql_fetch_array($result)){
		$lat = $record['lat'];
		$long = $record['long'];$id = $record['id'];
		$id = $record['id'];
		$moveu = $record['moveu'];

		$arr = array('lat' => $lat, 'long' => $long, 'id' => $id, 'moveu' => $moveu);
	}
	//create json array here
	echo "JSON: " . json_encode($arr) . "</br>";
?>