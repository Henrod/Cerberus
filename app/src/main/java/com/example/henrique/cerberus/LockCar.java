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

    private TextView tv_id;
    private TextView tv_lat;
    private TextView tv_long;
    private TextView tv_moveu;
    private TextView tv_time;
    private TextView tv_mode;
    private String json;

    private int id;

    Timer timer;

    public static RetrieveData retrieveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        tv_id = (TextView) findViewById(R.id.id);
        tv_lat = (TextView) findViewById(R.id.lat);
        tv_long = (TextView) findViewById(R.id._long);
        tv_moveu = (TextView) findViewById(R.id.moveu);
        tv_time = (TextView) findViewById(R.id.time);
        tv_mode = (TextView) findViewById(R.id.mode);

        id = getIntent().getIntExtra("id", 0);
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
        String json_mode = jsonObject.getString("mode");
        tv_mode.setText("Modo de operação: " + json_mode);

        return null;
    }


    class RetrieveData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            //establish server socker

            try {
                URL server = new URL(MainActivity.ip_server + "get_infos.php?id_java=" + id);
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
                }
            });;

            return null;
        }
    }



    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            retrieveData = new RetrieveData();
                            retrieveData.execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 10000); //execute in every 30000 ms = 30s
    }

    public void start(View view){
        view.setVisibility(View.GONE);

        tv_id.setVisibility(View.VISIBLE);
        tv_lat.setVisibility(View.VISIBLE);
        tv_long.setVisibility(View.VISIBLE);
        tv_moveu.setVisibility(View.VISIBLE);
        tv_time.setVisibility(View.VISIBLE);
        tv_mode.setVisibility(View.VISIBLE);

        callAsynchronousTask();
    }

    void verifyLocation(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        String moveu = jsonObject.getString("moveu");
        Double time_server = jsonObject.getDouble("time");

        if (moveu.equals("Sim") || passed_time(time_server)){
            timer.cancel();
            Intent alert = new Intent(LockCar.this, Alert.class);
            alert.putExtra("id", id);
            startActivity(alert);
        }
    }

    private boolean passed_time(double time_server) {
        Date time = new Date();
        //double time_now = time.getTime();
        double time_now = 40000;

        return time_now >= 30000 + time_server;
    }

    public void configuration(View view) {
        Intent config = new Intent(LockCar.this, Configuration.class);
        config.putExtra("id", id);
        startActivity(config);
    }

}
