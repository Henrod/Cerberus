import pymysql.cursors

# Connect to the database
connection = pymysql.connect(host='localhost',
                             user='root',
                             password='12345',
                             db='cerberus_db',
                             charset='utf8mb4',
                             cursorclass=pymysql.cursors.DictCursor)

try:
    with connection.cursor() as cursor:
        # Create a new record
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
