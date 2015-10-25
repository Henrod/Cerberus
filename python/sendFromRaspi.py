import random
import sys
import math
from math import sin, cos, sqrt, atan2, radians
import pymysql.cursors

latitude = 19.99
longitude = 73.78


 
def distance_on_unit_sphere(lat1, lon1, lat2, lon2):
 
	R = 6373.0 #earth radius

	dlon = float(lon2) - float(lon1)
	dlat = float(lat2) - float(lat1)
	


	a = sin(dlat / 2)**2 + cos(float(lat1)) * cos(float(lat2)) * sin(dlon / 2)**2
	c = 2 * atan2(sqrt(a), sqrt(1 - a))

	distance = R * c
	return distance #distancia em metros



def truncate(f, n):
    '''Truncates/pads a float f to n decimal places without rounding'''
    s = '%.12f' % f
    i, p, d = s.partition('.')
    return '.'.join([i, (d+'0'*n)[:n]])


def randomLatLong(latitude,longitude): #lat e long iniciais
	dec_lat = random.random()/1000
	dec_lon = random.random()/1000
	return truncate(dec_lat+latitude,6),truncate(dec_lon+longitude,6)


def moveu ((lat1,lon1),(lat2,lon2)):
		epsulon = 2 #incerteza em metros
		if  ( abs(distance_on_unit_sphere(lat1,lon1,lat2,lon2) - epsulon) < 1.0) :
			return False
		else:
			return True



print moveu(randomLatLong(latitude,longitude),randomLatLong(latitude,longitude))

'''

randomLatLong (latitude,longitude) devolve uma tupla da forma (latitude2,longitude2) calculada a partir de uma pequena variavao a partir de latitude,longitude

a funcao moveu recebe como parametros duas tuplas de (latitude,longitude) e calcula uma aproximacao da distancia geodesica das duas. Uma aproximacao grosseira e errada mas que deve servir pra os nossos propositos. Se vc estiver lendo isso Henrique, your the boss.

'''

# Connect to the database
connection = pymysql.connect(host='us-cdbr-iron-east-02.cleardb.net',
                             user='be50af336a3134',
                             password='324da5dd',
                             db='heroku_34cfa57a696e63d',
                             charset='utf8mb4',
                             cursorclass=pymysql.cursors.DictCursor)


#mysql://be50af336a3134:324da5dd@us-cdbr-iron-east-02.cleardb.net/heroku_34cfa57a696e63d?reconnect=true

try:
    with connection.cursor() as cursor:
        # Create a new record
        sql = "CREATE TABLE usersteste (email VARCHAR(20) , password VARCHAR(20)"
        cursor.execute(sql)
        sql = "INSERT INTO `usersteste` (`email`, `password`) VALUES (%s, %s)"
        cursor.execute(sql, ('cerberus@lira.org', '123456'))

    # connection is not autocommit by default. So you must commit to save
    # your changes.
    connection.commit()

    with connection.cursor() as cursor:
        # Read a single record
        sql = "SELECT `id`, `password`, `email` FROM `usersteste` WHERE `email`=%s"
        cursor.execute(sql, ('cerberus@lira.org',))
        result = cursor.fetchone()
        print(result)
finally:
    connection.close()