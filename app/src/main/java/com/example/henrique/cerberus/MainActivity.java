package com.example.henrique.cerberus;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
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
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ActionBarActivity {

    TextView textView;
    TextView tv_id;
    TextView tv_lat;
    TextView tv_long;
    TextView tv_moveu;
    String json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textview);
        tv_id = (TextView) findViewById(R.id.id);
        tv_lat = (TextView) findViewById(R.id.lat);
        tv_long = (TextView) findViewById(R.id._long);
        tv_moveu = (TextView) findViewById(R.id.moveu);
    }

    private String decode(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);

        String json_id = jsonObject.getString("id");
        tv_id.setText("ID: " + json_id);
        String json_lat = jsonObject.getString("lat");
        tv_lat.setText("Latitude: " + json_lat);
        String json_long = jsonObject.getString("long");
        tv_long.setText("Longitude: " + json_long);
        String json_moveu = jsonObject.getString("moveu");
        tv_moveu.setText("Moveu: " + json_moveu);

        return null;
    }


    class RetrieveData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            //establish server socker

            try {
                URL server = new URL("http://10.0.7.12/cerberus/teste.php");
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
                        verifyLocation(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (json != null) {
                        textView.setText("JSON capturado: " + json);
                    } else {
                        textView.setText("Erro: Sem conexão com IP do servidor");
                    }
                }
            });

            return null;
        }
    }



    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            RetrieveData retrieveData = new RetrieveData();
                            // PerformBackgroundTask this class is the class that extends AsynchTask
                            retrieveData.execute();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 10000); //execute in every 30000 ms = 30s
    }

    public void start(View view){
        view.setVisibility(View.GONE);
        callAsynchronousTask();
    }

    void verifyLocation(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        String moveu = jsonObject.getString("moveu");

        if (moveu.equals("Sim")){
            startActivity(new Intent(MainActivity.this, Alert.class));
        }
    }
}
