package com.touchdown.app.smartassistant.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.touchdown.app.smartassistant.MapActivity;
import com.touchdown.app.smartassistant.R;
import com.touchdown.app.smartassistant.models.Reminder;
import com.touchdown.app.smartassistant.models.ReminderDao;

/**
 * Created by Pete on 11.8.2014.
 */
public class AlarmNotification {

    private static final int NOTIFICATION_ID = 1000;

    public static void buildAlarmNotification(Context context, Reminder reminder){
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MapActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = createNotification(context, pendingIntent, reminder);
        notificationManager.notify((int) reminder.getId(), notification);
    }

    private static Notification createNotification(Context context, PendingIntent pendingIntent, Reminder reminder){
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        return new NotificationCompat.Builder(context)
                .setContentTitle("Proximity alert")
                .setTicker("Location reminder fired")
                .setVibrate(new long[] {0, 1000, 500, 1000, 500, 1000})
               // .setDefaults(Notification.DEFAULT_SOUND)
                .setSound(alarmSound)
                .setContentText(reminder.getContent())
                .setSmallIcon(R.drawable.ic_drawer)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setLights(Notification.DEFAULT_LIGHTS, 1500, 1500)
                .build();
    }

    public static void cancelNotification(Context context, Reminder reminder){
        if (Context.NOTIFICATION_SERVICE!=null) {
            NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancel((int) reminder.getId());
        }
    }
}
