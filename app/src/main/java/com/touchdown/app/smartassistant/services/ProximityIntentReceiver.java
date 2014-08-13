package com.touchdown.app.smartassistant.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.touchdown.app.smartassistant.MapActivity;
import com.touchdown.app.smartassistant.R;

/**
 * Created by Pete on 7.8.2014.
 */
public class ProximityIntentReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        String key = LocationManager.KEY_PROXIMITY_ENTERING;
        boolean entering = intent.getBooleanExtra(key, false);
        if(entering){
            Log.d(getClass().getSimpleName(), "entering");
            Toast.makeText(context, "Entered!!!", Toast.LENGTH_LONG).show();
        }else{
            Log.d(getClass().getSimpleName(), "exiting");
            Toast.makeText(context, "Exited!!!", Toast.LENGTH_LONG).show();
        }


        Intent alarmServiceIntent = new Intent(context, HandleAlarmService.class);
        alarmServiceIntent.putExtra("reminderID", intent.getLongExtra("reminderID", -1));
        startWakefulService(context, alarmServiceIntent);
    }
}
