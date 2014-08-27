package com.touchdown.app.smartassistant.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.touchdown.app.smartassistant.models.Task;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class TaskActivator extends Service {
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 50; // in
    // Meters
    private static final long HOUR_IN_MS = 1000*60*60;
    private static final long TEN_SECONDS  = 1000 * 10;
    // private static final long MINIMUM_TIME_BETWEEN_UPDATES = 60000; // in
    // Milliseconds

    private TaskManager taskManager;
    private List<Task> tasks;
    protected LocationManager locationManager;
    private MyLocationListener locationListener;

    protected int counter = 0;

    public TaskActivator() {
    }
                                ////TODO stop the service somewhere when task is turned off or removed!!!!
    @Override
    public void onCreate()
    {
        super.onCreate();
        this.taskManager = TaskManager.getInstance(this);
        subscribeToLocationUpdates();

        Toast.makeText(this, "Location helper Service created ...", Toast.LENGTH_SHORT)
                .show();
    }

    private void subscribeToLocationUpdates(){
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        locationListener = new MyLocationListener();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
                TEN_SECONDS,
                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                criteria,
                locationListener,
                null);
    }

    public static boolean startIfNeeded(Task task){
        Context ctx = ApplicationContextProvider.getAppContext();
        MyLocationProvider locationProvider = new MyLocationProvider(ctx);
        if(task.isActive() && locationProvider.isUserInLocation(task.getLocation().getLatLng(), task.getLocation().getRadius())){
            ctx.startService(new Intent(ctx, TaskActivator.class));
            return true;
        }
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        tasks = taskManager.getAllTasks();
        Toast.makeText(this, "Location helper onStartCommand called ...", Toast.LENGTH_SHORT)
                .show();
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        locationManager.removeUpdates(locationListener);
        Toast.makeText(this, "Location helper Service destroyed ...", Toast.LENGTH_LONG)
                .show();
        super.onDestroy();

    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class MyLocationListener implements LocationListener{
        public void onLocationChanged(Location location) {
            String message = String.format(
                    "New Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude());
            Toast.makeText(TaskActivator.this, message, Toast.LENGTH_LONG)
                    .show();

            if(setAlarmsForPendingTasks()){ //if no pending tasks left
                stopSelf();
            }
        }

        private boolean setAlarmsForPendingTasks(){
            boolean noPendingTasks = true;
            for (Task task: tasks){
                if(task.getLocation().isPending()){
                    MyLocationProvider locationProvider = new MyLocationProvider(TaskActivator.this);

                    boolean userStillInLocation = locationProvider.
                            isUserInLocation(task.getLocation().getLatLng(), task.getLocation().getRadius());
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

