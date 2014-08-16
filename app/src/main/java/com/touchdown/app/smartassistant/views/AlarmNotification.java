package com.touchdown.app.smartassistant.views;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.touchdown.app.smartassistant.MapActivity;
import com.touchdown.app.smartassistant.R;
import com.touchdown.app.smartassistant.models.Reminder;

/**
 * Created by Pete on 11.8.2014.
 */
public class AlarmNotification extends ReminderNotification {

    public AlarmNotification(Context context, Reminder reminder){
        super(context, reminder);
    }

    @Override
    protected Notification createNotification(){
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        return new NotificationCompat.Builder(mContext)
                .setContentTitle("Proximity alert")
                .setTicker("Location reminder fired")
                .setVibrate(new long[] {0, 1000, 500, 1000, 500, 1000})
               // .setDefaults(Notification.DEFAULT_SOUND)
                .setSound(alarmSound)
                .setContentText(mReminder.getContent())
                .setSmallIcon(R.drawable.ic_drawer)
                .setAutoCancel(true)
                .setContentIntent(getMapActivityIntent())
                .setLights(Notification.DEFAULT_LIGHTS, 1500, 1500)
                .build();
    }
}
