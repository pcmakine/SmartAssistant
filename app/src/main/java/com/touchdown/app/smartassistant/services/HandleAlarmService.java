package com.touchdown.app.smartassistant.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.LocationManager;

import com.touchdown.app.smartassistant.models.Task;

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
            String key = LocationManager.KEY_PROXIMITY_ENTERING;
            boolean entering = intent.getBooleanExtra(key, false);
            long id = intent.getLongExtra("reminderID", -1);

            if(id != -1){
                TaskManager taskManager = TaskManager.getInstance(this);
                Task task = taskManager.findTaskById(id);
                task.executeActions();
                task.turnAllActionsOff();
                taskManager.update(task);
            }
            ProximityIntentReceiver.completeWakefulIntent(intent);
        }
    }
}
