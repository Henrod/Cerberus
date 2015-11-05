import random
import sys
import math
from time import sleep
from math import sin, cos, sqrt, atan2, radians
import pymysql.cursors

def distance_on_unit_sphere(lat1, lon1, lat2, lon2):
 
    R = 6373.0 #earth radius

    dlon = float(lon2) - float(lon1)
    dlat = float(lat2) - float(lat1)

    a = sin(dlat / 2)**2 + cos(float(lat1)) * cos(float(lat2)) * sin(dlon / 2)**2
    c = 2 * atan2(sqrt(a), sqrt(1 - a))

    distance = R * c
    print "Distancia eh",truncate(distance,2) 
    return distance #distancia em metros

def truncate(f, n):
    '''Truncates/pads a float f to n decimal places without rounding'''
    s = '%.12f' % f
    i, p, d = s.partition('.')
    return '.'.join([i, (d+'0'*n)[:n]])


def randomLatLong(latitude,longitude): #lat e long iniciais
    lat = float(latitude)
    lon = float(longitude)
    dec_lat = random.random()/1000
    dec_lon = random.random()/1000
    return truncate(dec_lat+lat,6),truncate(dec_lon+lon,6)


def moveu ((lat1,lon1),(lat2,lon2)):
        epsulon = 2 #incerteza em metros
        
        if  ( abs(distance_on_unit_sphere(lat1,lon1,lat2,lon2) - epsulon) < abs(1.0 + epsulon)) :
            
            return "Nao"
        else:
            return "Sim"


'''

randomLatLong (latitude,longitude) devolve uma tupla da forma (latitude2,longitude2) calculada a partir de uma pequena variavao a partir de latitude,longitude

a funcao moveu recebe como parametros duas tuplas de (latitude,longitude) e calcula uma aproximacao da distancia geodesica das duas. Uma aproximacao grosseira e errada mas que deve servir pra os nossos propositos. Se vc estiver lendo isso Henrique, your the boss.

'''
i = 0
'''parametros iniciais'''
longitude = -46.59
latitude = -23.62
posInicial = randomLatLong(latitude,longitude)

posAtual = posInicial
# Connect to the database
'''
connection = pymysql.connect(host='us-cdbr-iron-east-02.cleardb.net',
                             user='be50af336a3134',
                             password='324da5dd',
                             db='heroku_34cfa57a696e63d',
                             charset='utf8mb4',
                             cursorclass=pymysql.cursors.DictCursor)

'''

connection = pymysql.connect(host='localhost',
                             user='root',
                             password='12345',
                             db='cerberus_db',
                             charset='utf8mb4',
                             cursorclass=pymysql.cursors.DictCursor)

#mysql://be50af336a3134:324da5dd@us-cdbr-iron-east-02.cleardb.net/heroku_34cfa57a696e63d?reconnect=true

id_do_raspi = 123


while (True): #loop principal

    novaPos = randomLatLong(posAtual[0],posAtual[1])

    moveuBool = moveu(posInicial,posAtual) #bool que fala se moveu ou nao
    
    posAtual = novaPos #atualiza as coisas

    try:
        with connection.cursor() as cursor:
            # Create a new record
            
            sql = "UPDATE `users` SET `lat` = %s , `long` = %s  , `moveu` = %s WHERE `id_rasp` = %s"
            cursor.execute(sql, (str(novaPos[0]), str(novaPos[1]), moveuBool,str(id_do_raspi)))



        # connection is not autocommit by default. So you must commit to save
        # your changes.
        connection.commit()

        with connection.cursor() as cursor:
            # Read a single record
            sql = "SELECT `id`, `lat`, `long` ,`moveu` FROM `users` WHERE `id_rasp`=%s"
            cursor.execute(sql, id_do_raspi)
            result = cursor.fetchone()
            print(result)

    finally:
        pass

    sleep(10) #roda a cada 5 segundos

connection.close()
