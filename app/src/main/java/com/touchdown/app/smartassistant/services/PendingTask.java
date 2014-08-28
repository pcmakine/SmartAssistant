package com.touchdown.app.smartassistant.services;

import android.content.Context;

import com.touchdown.app.smartassistant.models.Task;
import com.touchdown.app.smartassistant.models.TriggerLocation;

/**
 * Created by Pete on 28.8.2014.
 */
public class PendingTask {

    public static Task updatePendingStatus(Task task){
        TriggerLocation location = task.getLocation();

        if(location != null && shouldBePending(task)){
            location.setPending(true);
        } else{
            location.setPending(false);
        }
        return task;
    }

    public static boolean shouldBePending(Task task){
        Context ctx = ApplicationContextProvider.getAppContext();
        MyLocationProvider locationProvider = new MyLocationProvider(ctx);
        if(task.isActive() && locationProvider.isUserInLocation(task.getLocation().getLatLng(), task.getLocation().getRadius())){
            return true;
        }
        return false;
    }
}
