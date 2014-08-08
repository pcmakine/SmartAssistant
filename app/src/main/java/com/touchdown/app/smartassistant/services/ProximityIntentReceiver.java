package com.touchdown.app.smartassistant.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.touchdown.app.smartassistant.MapActivity;

/**
 * Created by Pete on 7.8.2014.
 */
public class ProximityIntentReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 1000;

    @Override
    public void onReceive(Context context, Intent intent){
        String key = LocationManager.KEY_PROXIMITY_ENTERING;
        boolean entering = intent.getBooleanExtra(key, false);
        if(entering){
            Log.d(getClass().getSimpleName(), "entering");
        }else{
            Log.d(getClass().getSimpleName(), "exiting");
        }

        NotificationManager notificationManager =
                (NotificationManager) MapActivity.appCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
        Notification notification = createNotification(context, pendingIntent);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private Notification createNotification(Context context, PendingIntent pendingIntent){
        return new Notification.Builder(context)
                .setContentTitle("Proximity alert!!!")
                .setContentText("You are close to your reminder")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setLights(Notification.DEFAULT_LIGHTS, 1500, 1500)
                .build();
    }
}
