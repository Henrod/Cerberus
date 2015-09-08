package com.example.henrique.cerberus;

import android.app.Activity;
import android.app.AlarmManager;
import android.os.Bundle;

/**
 * Created by henrique on 08/09/15.
 */
public class Alert extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, );



    }
}
