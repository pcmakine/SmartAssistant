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

                executeActions(entering, task);
            }
            ProximityIntentReceiver.completeWakefulIntent(intent);
        }
    }

    private void executeActions(boolean entering, Task task){
        if((entering && taskHasLocationWithArrivalTrigger(task))
                || (!entering && taskHasLocationWithDepartureTrigger(task))){
            task.executeActions();
            task.turnAllActionsOff();
            TaskManager.getInstance(this).update(task);
        }
    }

    private boolean taskHasLocationWithArrivalTrigger(Task task){
        return task.getLocation() != null && task.getLocation().isArrivalTriggerOn();
    }

    private boolean taskHasLocationWithDepartureTrigger(Task task){
        return task.getLocation() != null && task.getLocation().isDepartureTriggerOn();
    }
}
