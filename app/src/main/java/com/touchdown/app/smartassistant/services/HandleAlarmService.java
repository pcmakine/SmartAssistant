package com.touchdown.app.smartassistant.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.touchdown.app.smartassistant.data.DbHelper;
import com.touchdown.app.smartassistant.models.Reminder;
import com.touchdown.app.smartassistant.models.ReminderDao;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class HandleAlarmService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.touchdown.app.smartassistant.services.action.FOO";
    private static final String ACTION_BAZ = "com.touchdown.app.smartassistant.services.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.touchdown.app.smartassistant.services.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.touchdown.app.smartassistant.services.extra.PARAM2";


    public HandleAlarmService() {
        super("HandleAlarmService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            long id = intent.getLongExtra("reminderID", -1);

            if(id != -1){
                ReminderDao reminderManager = new ReminderDao(new DbHelper(getApplicationContext()));
                Reminder reminder = reminderManager.getOne(id);
                reminder.turnOff();
                reminderManager.update(reminder);
                AlarmNotification.buildAlarmNotification(this, reminder);
            }
            ProximityIntentReceiver.completeWakefulIntent(intent);
        }
    }
}
