package com.example.henrique.cerberus;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ActionBarActivity {

    EditText et_login;
    String login, json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_login = (EditText) findViewById(R.id.et_login);
    }

    public void start_login(View view) throws JSONException {
        login = et_login.getText().toString();

        RetrieveData retrieveData = new RetrieveData();
        // PerformBackgroundTask this class is the class that extends AsynchTask
        retrieveData.execute();
    }

    class RetrieveData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            //establish server socker

            try {
                URL server = new URL("http://172.21.221.95/cerberus/get_login.php?login_java=" + login);//"http://10.0.7.12/cerberus/teste.php");
                BufferedReader in = new BufferedReader(new InputStreamReader(server.openStream()));
                json = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        decode(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            return null;
        }
    }

    //decodes JSON, put ID to lock activity and start it
    private void decode(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        int id = jsonObject.getInt("id");

        Intent lock = new Intent(MainActivity.this, LockCar.class);

        lock.putExtra("id", id);
        startActivity(lock);
    }

}
