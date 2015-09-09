package com.example.henrique.cerberus;

import android.app.admin.DevicePolicyManager;
import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by henrique on 08/09/15.
 */

// Class to connect with remote MySQL
public class JDBC extends AsyncTask{
    public static Connection con = null;
    int i = 0;

    public void connect() {
        new Thread(){
            @Override
            public void run() {
                super.run();

                String driver = "com.mysql.jdbc.Driver";
                try {
                    Class.forName(driver).newInstance();
                    //String connString = "jdbc:jtds:sqlserver://10.0.7.12 " +
                    //        ":cerberus_db;encrypt=false;user=root;password=12345;instance=SQLEXPRESS;";

                    String username = "appUser";
                    String password = "12345";
                    con = DriverManager.getConnection("jdbc:mysql://10.0.2.2", username, password);
                    Log.w("Data: ", "connectou");
                    Statement statement = con.createStatement();
                    //ResultSet set = statement.executeQuery("SELECT * FROM cerberus_db.users");
                    ResultSet resultSet = statement.executeQuery("SELECT lat FROM cerberus_db.users WHERE id = 1;");

                    while(resultSet.next()){
                        //Log.w("Data: ", set.getString(1));
                        resultSet.updateString("lat", "55");
                        resultSet.updateRow();
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        String driver = "com.mysql.jdbc.Driver";
        try {
            Class.forName(driver).newInstance();
            //String connString = "jdbc:jtds:sqlserver://10.0.7.12 " +
            //        ":cerberus_db;encrypt=false;user=root;password=12345;instance=SQLEXPRESS;";

            String username = "appUser";
            String password = "12345";
            con = DriverManager.getConnection("jdbc:mysql://10.0.2.2", username, password);
            Log.w("Data: ", "connectou");
            Statement statement = con.createStatement();
            ResultSet set = statement.executeQuery("SELECT * FROM cerberus_db.users");

            while(set.next()){
                Log.w("Data: ", set.getString(1));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
