package com.touchdown.app.smartassistant.services;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.touchdown.app.smartassistant.models.Task;

/**
 * Created by Pete on 11.8.2014.
 */
public class ProximityAlarmManager {
    private static final long PROX_ALERT_EXPIRATION = 1000*60*60*24*2; //in milliseconds two days todo change to -1 when ready to ship
    private static final String PROX_ALERT_INTENT = "com.touchdown.app.smartassistant.services.ProximityIntentReceiver";


    public static void updateAlert(Task task){
        saveAlert(task);
    }

    public static void saveAlert(Task task){
        if(task.getLocation() != null){
            LocationManager locationManager = (LocationManager) ApplicationContextProvider.getAppContext().getSystemService(Context.LOCATION_SERVICE);
            PendingIntent proximityIntent = constructPendingIntent(task.getId());
            locationManager.addProximityAlert(
                    task.getLocation().getLatLng().latitude,
                    task.getLocation().getLatLng().longitude,
                    task.getLocation().getRadius(),
                    PROX_ALERT_EXPIRATION,
                    proximityIntent);
        }
    }

    public static void removeAlert(long reminderId){
        LocationManager locationManager = (LocationManager) ApplicationContextProvider.getAppContext().getSystemService(Context.LOCATION_SERVICE);
        PendingIntent pIndent = constructPendingIntent(reminderId);
        locationManager.removeProximityAlert(pIndent);
    }

    private static PendingIntent constructPendingIntent(long reminderId){
        Intent intent = new Intent(PROX_ALERT_INTENT);
        intent.putExtra("reminderID", reminderId);
        //todo handle the case where the reminder id has grown too much to fit into int
        PendingIntent proximityIntent = PendingIntent.getBroadcast(ApplicationContextProvider.getAppContext(), (int) reminderId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return proximityIntent;
    }

}

