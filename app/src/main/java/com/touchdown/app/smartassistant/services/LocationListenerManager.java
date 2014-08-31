package com.touchdown.app.smartassistant.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.touchdown.app.smartassistant.models.Task;
import com.touchdown.app.smartassistant.services.MyLocationProvider;
import com.touchdown.app.smartassistant.services.TaskManager;

import java.util.List;

public class LocationListenerManager extends IntentService {

    public static final String LOG_TAG = LocationListenerManager.class.getSimpleName();

    public LocationListenerManager() {
        super("LocationListenerManager");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        List<Task> tasks = TaskManager.getInstance(this).getAllTasks();

        handleDepartureBroadcastAlarm(tasks);
        handleTaskActivator(tasks);
    }

    private void handleTaskActivator(List<Task> tasks){
        boolean noPendingTasks = true;

        for(Task task: tasks){
            if(task.isActive() && task.getLocation().isPending()){
                noPendingTasks = false;
            }
        }
        if(noPendingTasks){
            stopService(new Intent(this, TaskActivator.class));
        }else{
            startService(new Intent(this, TaskActivator.class));
        }
    }

    private void handleDepartureBroadcastAlarm(List<Task> tasks){
        if(!userInAreaOfActiveDepartureTask(tasks)){
            stopService(new Intent(this, DepartureTaskBroadcastAlarm.class));
        }else{
            startService(new Intent(this, DepartureTaskBroadcastAlarm.class));
        }
    }

    private boolean userInAreaOfActiveDepartureTask(List<Task> tasks){
        MyLocationProvider locationProvider = new MyLocationProvider(this);

        for(Task task: tasks){
            if(task.isActive() && task.getLocation().isDepartureTriggerOn()){
                if(locationProvider.isUserInLocation(task.getLocation().getLatLng(), task.getLocation().getRadius())){
                    return true;
                }
            }
        }
        Log.d(LOG_TAG, "User not in area for any active departure task");
        return false;
    }
}
