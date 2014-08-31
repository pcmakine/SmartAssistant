package com.touchdown.app.smartassistant.views;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;

import com.touchdown.app.smartassistant.models.Alarm;

/**
 * Created by Pete on 16.8.2014.
 */
public abstract class AbstractNotif {
    private static final long NOTIFICATION_ID = 100;
    protected Alarm mAlarm;
    protected Context mContext;

    public AbstractNotif(Context context, Alarm alarm){
        this.mAlarm = alarm;
        this.mContext = context.getApplicationContext();
    }

    protected abstract Notification createNotification();

    public void buildNotification(){
        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = createNotification();
        long notificationId = mAlarm == null ? NOTIFICATION_ID: mAlarm.getId();
        notificationManager.notify((int) notificationId, notification);
    }

    protected PendingIntent getMapActivityIntent(){
        Intent resultIntent = new Intent(mContext, Map.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(Map.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        return resultPendingIntent;
    }

    public void cancelNotification(){
        if (Context.NOTIFICATION_SERVICE!=null) {
            NotificationManager nMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            long notificationId = mAlarm == null ? NOTIFICATION_ID: mAlarm.getId();
            nMgr.cancel((int) notificationId);
        }
    }

}
