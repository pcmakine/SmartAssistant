package com.touchdown.app.smartassistant.services;

import android.app.IntentService;
import android.content.Intent;

import com.touchdown.app.smartassistant.models.Reminder;
import com.touchdown.app.smartassistant.views.AlarmNotification;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class HandleAlarmService extends IntentService {

    public HandleAlarmService() {
        super("HandleAlarmService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            long id = intent.getLongExtra("reminderID", -1);

            if(id != -1){
                ReminderManager reminderManager = ReminderManager.getInstance(this);
                Reminder reminder = reminderManager.getOne(id);
                reminder.turnOff();
                reminderManager.update(reminder);
                new AlarmNotification(this, reminder).buildNotification();
            }
            ProximityIntentReceiver.completeWakefulIntent(intent);
        }
    }
}
