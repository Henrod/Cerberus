package com.example.henrique.cerberus;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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


/**
 * Created by henrique on 08/09/15.
 */
public class Alert extends FragmentActivity {

    private GoogleMap googleMap;
    private int id;
    private static long longitude, latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert);

        id = getIntent().getIntExtra("id", 0);
        setUpMap();
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

    private void setMarker() {
            LatLng coordinate = new LatLng(latitude, longitude);
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 17);
            googleMap.animateCamera(yourLocation);
            googleMap.addMarker(new MarkerOptions().position(coordinate));
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
                            setMarker();
                        }
                    } else
                        Toast.makeText(Alert.this, "Não foi possível obter os dados :(", Toast.LENGTH_LONG).show();

                }
            });

            return null;
        }

        private void decode(String json) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                latitude = jsonObject.getLong("lat");
                longitude = jsonObject.getLong("long");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}