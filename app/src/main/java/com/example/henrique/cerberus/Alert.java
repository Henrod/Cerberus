package com.example.henrique.cerberus;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Alert extends FragmentActivity implements OnMapReadyCallback {

    private String id_rasp;
    private static double longitude, latitude;
    private MapFragment mapFragment;
    private static RetrieveData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert);

        id_rasp = getIntent().getStringExtra("id_rasp");

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        startAlarm();
    }

    @Override
    protected void onStart() {
        super.onStart();

        boolean display_signal = getIntent().getBooleanExtra("display_signal", false);
        if (!display_signal)
            findViewById(R.id.signal).setVisibility(View.GONE);

        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                data = new RetrieveData();
                data.execute();
            }
        };

        timer.schedule(doAsynchronousTask, 0, 10000); //execute in every 30000 ms = 30s
    }

    private void startAlarm() {
        Intent intent = new Intent(Alert.this, BroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(Alert.this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        Time now = new Time();
        now.setToNow();
        calendar.set(Calendar.HOUR_OF_DAY, now.hour);
        calendar.set(Calendar.MINUTE, now.minute);
        calendar.set(Calendar.SECOND, now.second);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        data.cancel(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        data.cancel(true);
    }

    public void stopNotification(View view) {
        data.cancel(true);
        Intent go_back = new Intent(Alert.this, LockCar.class);
        go_back.putExtra("id_rasp", id_rasp);
        startActivity(go_back);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng coordinate = new LatLng(latitude, longitude);

        String loc = "(" + String.valueOf(latitude) + ", " + String.valueOf(longitude) + ")";
        Log.d("loc", loc);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 17);
        googleMap.animateCamera(yourLocation);
        googleMap.addMarker(new MarkerOptions().position(coordinate).title(loc));
    }

    class RetrieveData extends AsyncTask<String, String, String> {

        String json = "";

        @Override
        protected String doInBackground(String... params) {
            Log.d("debug", "run");
            try {
                URL server = new URL(MainActivity.IP_SERVER + "get_location.php?id_java=" + id_rasp);
                BufferedReader in = new BufferedReader(new InputStreamReader(server.openStream()));
                json = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (json != null) {
                        decode(json);
                    } else
                        Toast.makeText(Alert.this, "Não foi possível obter os dados :(", Toast.LENGTH_LONG).show();

                }
            });
            return null;
        }



        private void decode(final String json) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                latitude = Double.parseDouble(jsonObject.getString("lat"));
                longitude = Double.parseDouble(jsonObject.getString("long"));
                Log.d("loc s", jsonObject.getDouble("lat") + " " + jsonObject.getDouble("long"));
                if (mapFragment.getMap() != null) {
                    mapFragment.getMap().clear();
                    mapFragment.getMapAsync(Alert.this);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}