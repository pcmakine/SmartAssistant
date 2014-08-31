package com.touchdown.app.smartassistant.services;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.touchdown.app.smartassistant.models.Task;
import com.touchdown.app.smartassistant.services.HandleAlarmService;
import com.touchdown.app.smartassistant.services.MyLocationProvider;

import java.util.ArrayList;
import java.util.List;

public class DepartureTaskBroadcastAlarm extends LocationListenerService {

    private MyLocationListener locationListener;

    public DepartureTaskBroadcastAlarm() {
    }

    @Override
    public void onDestroy(){
        locationManager.removeUpdates(locationListener);
        super.onDestroy();
    }

    @Override
    protected LocationListener createListener() {
        this.locationListener = new MyLocationListener();
        return locationListener;
    }

    private List<Task> checkForTriggeredDepartureTasks(Location location){
        List<Task> triggeredDepartures = new ArrayList<Task>();
        MyLocationProvider locationProvider = new MyLocationProvider(DepartureTaskBroadcastAlarm.this);

        for(Task task: tasks){
            if(task.isActive() && task.getLocation().isDepartureTriggerOn()){
                if(!locationProvider.isLocationInArea(location,
                        task.getLocation().getLatLng(), task.getLocation().getRadius())){
                    triggeredDepartures.add(task);
                }
            }
        }
        return triggeredDepartures;
    }

    private void showAlarmForTriggeredTasks(List<Task> triggered) {
        for(Task task: triggered){
            Intent alarmServiceIntent = new Intent(this, HandleAlarmService.class);
            alarmServiceIntent.putExtra("reminderID", task.getId());
            alarmServiceIntent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);

            startService(alarmServiceIntent);
        }
    }



    private class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            String message = String.format(
                    "New Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude());
            Toast.makeText(DepartureTaskBroadcastAlarm.this, message, Toast.LENGTH_LONG)
                    .show();

            List<Task> triggered = checkForTriggeredDepartureTasks(location);

            showAlarmForTriggeredTasks(triggered);
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

/*        @Override
        public void update(Observable observable, Object data) {        //database has been updated when this method is called
            tasks = taskManager.getAllTasks();

            if(!userInAreaOfActiveDepartureTask()){
                stopSelf();
            }
        }*/
    }
}
