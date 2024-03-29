package com.touchdown.app.smartassistant.services;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.touchdown.app.smartassistant.models.Task;
import com.touchdown.app.smartassistant.services.MyLocationProvider;
import com.touchdown.app.smartassistant.services.TaskManager;

/**
 * Activates proximity alarms for tasks that were created inside the area they are supposed to fire
 */
public class TaskActivator extends LocationListenerService {

    private MyLocationListener locationListener;

    public TaskActivator() {
    }

    @Override
    protected LocationListener createListener() {
        this.locationListener = new MyLocationListener();
        return locationListener;
    }


    @Override
    public void onDestroy(){
        locationManager.removeUpdates(locationListener);
        super.onDestroy();
    }



    private class MyLocationListener implements LocationListener{
        public void onLocationChanged(Location location) {
            String message = String.format(
                    "New Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude());
            Toast.makeText(TaskActivator.this, message, Toast.LENGTH_LONG)
                    .show();

            if(setAlarmsForPendingTasks(location)){ //if no pending tasks left
                stopSelf();
            }
        }

        private boolean setAlarmsForPendingTasks(Location location){
            boolean noPendingTasks = true;
            for (Task task: tasks){
                if(task.getLocation().isPending()){
                    MyLocationProvider locationProvider = new MyLocationProvider(TaskActivator.this);

                    boolean userStillInLocation = locationProvider.
                            isLocationInArea(location, task.getLocation().getLatLng(), task.getLocation().getRadius());
                    if(!userStillInLocation){
                        task.getLocation().setPending(false);
                        TaskManager.getInstance(TaskActivator.this).update(task);                   //the updatefunction
                    }else{
                        noPendingTasks = false;
                    }
                }
            }
            return noPendingTasks;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

    }
}

