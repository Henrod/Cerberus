package com.example.henrique.cerberus;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by henrique on 18/09/15.
 */
public class LockCar extends Activity {

    TextView textView;
    TextView tv_id;
    TextView tv_lat;
    TextView tv_long;
    TextView tv_moveu;
    TextView tv_time;
    String json;

    JDBC jdbc;

    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        textView = (TextView) findViewById(R.id.textview);
        tv_id = (TextView) findViewById(R.id.id);
        tv_lat = (TextView) findViewById(R.id.lat);
        tv_long = (TextView) findViewById(R.id._long);
        tv_moveu = (TextView) findViewById(R.id.moveu);
        tv_time = (TextView) findViewById(R.id.time);

        id = getIntent().getIntExtra("id", 0);

        jdbc = new JDBC();
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
        Double json_time = jsonObject.getDouble("time");
        tv_time.setText("Tempo servidor: " + json_time);

        //Date date_now = new Date();
        //tv_time.setText("Time now: " + date_now.getTime());

        return null;
    }


    class RetrieveData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            //establish server socker

            try {
                URL server = new URL("http://172.21.221.95/cerberus/get_infos.php?id_java=" + id);//"http://10.0.7.12/cerberus/teste.php");
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
        Double time_server = jsonObject.getDouble("time");

        if (moveu.equals("Sim") || passed_time(time_server)){
            startActivity(new Intent(LockCar.this, Alert.class));
        }
    }

    private void resetWatchdog() {
        jdbc.connect();
    }

    private boolean passed_time(double time_server) {
        Date time = new Date();
        //double time_now = time.getTime();
        double time_now = 40000;

        return time_now >= 30000 + time_server;
    }
}
