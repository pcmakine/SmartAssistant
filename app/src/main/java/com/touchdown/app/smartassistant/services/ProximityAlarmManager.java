package com.touchdown.app.smartassistant.services;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.touchdown.app.smartassistant.MapActivity;
import com.touchdown.app.smartassistant.models.ReminderDao;

/**
 * Created by Pete on 11.8.2014.
 */
public class ProximityAlarmManager {
    private static final long PROX_ALERT_EXPIRATION = 1000*60*60; // an hour//1000*60*60*24*2; //in milliseconds two days
    private static final String PROX_ALERT_INTENT = "com.touchdown.app.smartassistant.services.ProximityIntentReceiver";


    public static void updateAlert(ReminderDao reminder){
        saveAlert(reminder);
    }

    public static void saveAlert(ReminderDao reminder){
        if(reminder.getLocation() != null){
            LocationManager locationManager = (LocationManager) MapActivity.appCtx.getSystemService(Context.LOCATION_SERVICE);
            PendingIntent proximityIntent = constructPendingIntent(reminder);
            locationManager.addProximityAlert(reminder.getLocation().getLatLng().latitude,
                    reminder.getLocation().getLatLng().longitude,
                    reminder.getLocation().getRadius(),
                    PROX_ALERT_EXPIRATION,
                    proximityIntent);
        }
    }

    public static void removeAlert(ReminderDao reminder){
        LocationManager locationManager = (LocationManager) MapActivity.appCtx.getSystemService(Context.LOCATION_SERVICE);
        PendingIntent pIndent = constructPendingIntent(reminder);
        locationManager.removeProximityAlert(pIndent);
    }

    private static PendingIntent constructPendingIntent(ReminderDao reminder){
        Intent intent = new Intent(MapActivity.appCtx, ProximityIntentReceiver.class);
        intent.putExtra("reminderID", reminder.getId());
        //todo handle the case where the reminder id has grown too much to fit into int
        PendingIntent proximityIntent = PendingIntent.getBroadcast(MapActivity.appCtx, (int) reminder.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return proximityIntent;
    }

}

