package com.example.henrique.cerberus;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.gms.maps.SupportMapFragment;
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


/**
 * Created by henrique on 08/09/15.
 */
public class Alert extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private int id;
    private static long longitude, latitude;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert);

        id = getIntent().getIntExtra("id", 0);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        (new RetrieveData()).execute();

        startAlarm();
    }

    private void setUpMap() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            googleMap = ((SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
    }

    private GoogleMap setMarker() {
            LatLng coordinate = new LatLng(-46.6, -23.6);
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 17);
            googleMap.animateCamera(yourLocation);
            googleMap.addMarker(new MarkerOptions().position(coordinate));

            return googleMap;
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

    public void stopNotification(View view) {
        LockCar.retrieveData.cancel(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng coordinate = new LatLng(-46.6, -23.6);
        Log.d("loca", "(" + latitude + ", " + longitude + ")");
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 17);
        googleMap.animateCamera(yourLocation);
        googleMap.addMarker(new MarkerOptions().position(coordinate));
    }

    class RetrieveData extends AsyncTask<String, String, String> {

        String json = "";

        @Override
        protected String doInBackground(String... params) {
            //establish server socker

            try {
                URL server = new URL(MainActivity.ip_server + "get_location.php?id_java=" + id);
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
                        if (googleMap != null) {
                            onMapReady(setMarker());
                        }
                    } else
                        Toast.makeText(Alert.this, "Não foi possível obter os dados :(", Toast.LENGTH_LONG).show();

                }
            });

            return null;
        }



        private void decode(final String json) {
            final Handler handler = new Handler();
            timer = new Timer();
            TimerTask doAsynchronousTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                latitude = jsonObject.getLong("lat");
                                longitude = jsonObject.getLong("long");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            };
            timer.schedule(doAsynchronousTask, 0, 10000); //execute in every 30000 ms = 30s
        }
    }
}