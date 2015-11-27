package com.example.henrique.cerberus;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by henrique on 18/09/15.
 */
public class LockCar extends Activity {

    private TextView tv_id;
    private TextView tv_id_rasp;
    private TextView tv_lat;
    private TextView tv_long;
    private TextView tv_moveu;
    private TextView tv_time;
    private TextView tv_mode;
    private TextView tv_warning;
    private TextView tv_address;
    private String json;

    private String id_rasp;
    private static boolean started = false;

    Timer timer;

    public static RetrieveData retrieveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        tv_id       = (TextView) findViewById(R.id.id);
        tv_id_rasp  = (TextView) findViewById(R.id.id_rasp);
        tv_lat      = (TextView) findViewById(R.id.lat);
        tv_long     = (TextView) findViewById(R.id._long);
        tv_moveu    = (TextView) findViewById(R.id.moveu);
        tv_time     = (TextView) findViewById(R.id.time);
        tv_mode     = (TextView) findViewById(R.id.mode);
        tv_warning  = (TextView) findViewById(R.id.warning);
        tv_address  = (TextView) findViewById(R.id.address);

        id_rasp = getIntent().getStringExtra("id_rasp");
        started = false;
        started = getIntent().getBooleanExtra("started", false);

        if (started)
            start(findViewById(R.id.lock_car));
    }

    private String decode(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);

        String json_id = jsonObject.getString("id");
        tv_id.setText(json_id);
        String json_id_rasp = jsonObject.getString("id_rasp");
        tv_id_rasp.setText(json_id_rasp);
        String json_lat = jsonObject.getString("lat");
        tv_lat.setText(json_lat);
        String json_long = jsonObject.getString("long");
        tv_long.setText(json_long);
        String json_moveu = jsonObject.getString("moveu");
        tv_moveu.setText(json_moveu);
        String json_mode = jsonObject.getString("mode").equals("S") ? "Segurança total" : "Manobrista";
        tv_mode.setText(json_mode);

        Long json_time = (long) jsonObject.getDouble("time");
        Date date = new Date(json_time*1000);
        tv_time.setText(date.toString());

        tv_address.setText(get_street_name(
                Double.parseDouble(json_lat),
                Double.parseDouble(json_long)));

        return null;
    }


    class RetrieveData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            //establish server socker

            try {
                URL server = new URL(MainActivity.IP_SERVER + "get_infos.php?id_java=" + id_rasp);
                BufferedReader in = new BufferedReader(new InputStreamReader(server.openStream()));
                json = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (json != null) {
                            decode(json);
                            verifyLocation(json);
                        }
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

        (findViewById(R.id.table)).setVisibility(View.VISIBLE);
        (findViewById(R.id.open_map_button)).setVisibility(View.VISIBLE);
        (findViewById(R.id.config_button)).setVisibility(View.VISIBLE);
        (findViewById(R.id.logout)).setVisibility(View.VISIBLE);

        callAsynchronousTask();
    }

    private void verifyLocation(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        String moveu = jsonObject.getString("moveu");
        Double time_server = jsonObject.getDouble("time");

        if ((moveu.equals("Sim") || passed_time(time_server))){
            if (started) {
                tv_warning.setTextColor(Color.RED);
                tv_warning.setText("SEU CARRO ESTÁ EM MOVIMENTO!!!");
            } else {
                timer.cancel();
                retrieveData.cancel(true);
                Intent alert = new Intent(LockCar.this, Alert.class);
                alert.putExtra("id_rasp", id_rasp);
                alert.putExtra("display_signal", true);
                startActivity(alert);
            }
        } else {
            tv_warning.setTextColor(Color.BLUE);
            tv_warning.setText("Seu carro está seguro :)");
        }
    }

    public void startMap(View view) {
        Intent map = new Intent(LockCar.this, Alert.class);
        map.putExtra("id_rasp", id_rasp);
        map.putExtra("display_signal", false);

        startActivity(map);
    }

    private boolean passed_time(double time_server) {
        long time_now = System.currentTimeMillis()/1000L;

        return time_now >= 30 + time_server;
    }

    public void configuration(View view) {
        Intent config = new Intent(LockCar.this, Configuration.class);
        config.putExtra("id_rasp", id_rasp);
        startActivity(config);
    }

    private String get_street_name(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(LockCar.this);
        String street = "Não descoberto";
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                street = address.getThoroughfare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return street;
    }

    public void logout(View view) {
        timer.cancel();
        retrieveData.cancel(true);
        startActivity(new Intent(LockCar.this, MainActivity.class));
    }

}
