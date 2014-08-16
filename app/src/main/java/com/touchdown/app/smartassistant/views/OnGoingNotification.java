package com.touchdown.app.smartassistant.views;

import android.app.Notification;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.touchdown.app.smartassistant.ApplicationContextProvider;
import com.touchdown.app.smartassistant.R;
import com.touchdown.app.smartassistant.models.Reminder;
import com.touchdown.app.smartassistant.services.ReminderManager;

/**
 * Created by Pete on 16.8.2014.
 */
public class OnGoingNotification extends ReminderNotification {

    private int reminderCount;

    public OnGoingNotification(Context context, Reminder reminder) {
        super(context, reminder);
    }

    public OnGoingNotification(Context context, Reminder reminder, int reminderCount){
        super(context, reminder);
        this.reminderCount = reminderCount;
    }

    @Override
    protected Notification createNotification() {
        return new NotificationCompat.Builder(mContext)
                .setContentTitle("Location helper enabled")
                .setContentText("You have " + reminderCount + " active reminders")
                .setSmallIcon(R.drawable.ic_drawer)
                .setOngoing(true)
                .setContentIntent(getMapActivityIntent())
                .setLights(Notification.DEFAULT_LIGHTS, 1500, 1500)
                .build();
    }

    public static void updateNotification(){
        ReminderManager rManager = ReminderManager.getInstance(ApplicationContextProvider.getAppContext());
        int activeReminders = rManager.getActiveReminderCount();
        OnGoingNotification notif = new OnGoingNotification(ApplicationContextProvider.getAppContext(), null, activeReminders);
        if(activeReminders > 0){
            notif.buildNotification();
        }else{
            notif.cancelNotification();
        }
    }
}
