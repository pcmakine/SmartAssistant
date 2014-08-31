package com.touchdown.app.smartassistant.services;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.widget.Toast;

import com.touchdown.app.smartassistant.models.Task;

import java.util.List;


/**
 * Created by Pete on 28.8.2014.
 */
public abstract class LocationListenerService extends Service {
    public static final String LOG_TAG = LocationListenerService.class.getSimpleName();
    protected static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 30; // in Meters
    protected static final long HOUR_IN_MS = 1000*60*60;
    protected static final long TEN_SECONDS = 1000 * 10;

    protected TaskManager taskManager;
    protected List<Task> tasks;
    protected LocationManager locationManager;
    private boolean testingOnEmulator;      //todo remove this when ready to ship!


    @Override
    public void onCreate(){
        super.onCreate();

        this.taskManager = TaskManager.getInstance(this);
        subscribeToLocationUpdates();
        Toast.makeText(this, this.getClass().getSimpleName() + " OnCreate called ...", Toast.LENGTH_SHORT)
                .show();
    }


    private void subscribeToLocationUpdates(){
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        registerListener(criteria);
    }

    protected final void registerListener(Criteria criteria) {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(testingOnEmulator){
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    TEN_SECONDS,
                    MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,

                    createListener(),
                    null);
        }else{
            locationManager.requestLocationUpdates(
                    TEN_SECONDS,
                    MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                    criteria,
                    createListener(),
                    null);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        tasks = taskManager.getAllTasks();
        Toast.makeText(this, this.getClass().getSimpleName() + " OnStartCommand called ...", Toast.LENGTH_SHORT)
                .show();
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        Toast.makeText(this, this.getClass().getSimpleName() + " Service destroyed ...", Toast.LENGTH_LONG)
                .show();
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected abstract LocationListener createListener();
}


