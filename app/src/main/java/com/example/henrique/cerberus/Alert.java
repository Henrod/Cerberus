package com.example.henrique.cerberus;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;

import java.util.Calendar;

/**
 * Created by henrique on 08/09/15.
 */
public class Alert extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert);

        startAlarm();
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
}
