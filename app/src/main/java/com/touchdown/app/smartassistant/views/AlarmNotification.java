package com.touchdown.app.smartassistant.views;

import android.app.Notification;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.touchdown.app.smartassistant.R;
import com.touchdown.app.smartassistant.newdb.NotificationReminder;

/**
 * Created by Pete on 11.8.2014.
 */
public class AlarmNotification extends AbstractNotif {

    public AlarmNotification(Context context, NotificationReminder reminder){
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
