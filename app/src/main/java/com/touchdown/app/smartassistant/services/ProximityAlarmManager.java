package com.touchdown.app.smartassistant.services;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.touchdown.app.smartassistant.ApplicationContextProvider;
import com.touchdown.app.smartassistant.models.Reminder;

import java.util.List;

/**
 * Created by Pete on 11.8.2014.
 */
public class ProximityAlarmManager {
    private static final long PROX_ALERT_EXPIRATION = 1000*60*60; // an hour//1000*60*60*24*2; //in milliseconds two days
    private static final String PROX_ALERT_INTENT = "com.touchdown.app.smartassistant.services.ProximityIntentReceiver";


    public static void updateAlert(Reminder reminder){
        saveAlert(reminder);
    }

    public static void saveAlert(Reminder reminder){
        if(reminder.getLocation() != null){
            LocationManager locationManager = (LocationManager) ApplicationContextProvider.getAppContext().getSystemService(Context.LOCATION_SERVICE);
            PendingIntent proximityIntent = constructPendingIntent(reminder.getId());
            locationManager.addProximityAlert(reminder.getLocation().getLatLng().latitude,
                    reminder.getLocation().getLatLng().longitude,
                    reminder.getLocation().getRadius(),
                    PROX_ALERT_EXPIRATION,
                    proximityIntent);
        }
    }

    public static void removeAlert(long reminderId){
        LocationManager locationManager = (LocationManager) ApplicationContextProvider.getAppContext().getSystemService(Context.LOCATION_SERVICE);
        PendingIntent pIndent = constructPendingIntent(reminderId);
        locationManager.removeProximityAlert(pIndent);
    }

    //todo find out how to make sure that the app context is always available (put it to the launcher activity maybe?)
    private static PendingIntent constructPendingIntent(long reminderId){
        Intent intent = new Intent(ApplicationContextProvider.getAppContext(), ProximityIntentReceiver.class);
        intent.putExtra("reminderID", reminderId);
        //todo handle the case where the reminder id has grown too much to fit into int
        PendingIntent proximityIntent = PendingIntent.getBroadcast(ApplicationContextProvider.getAppContext(), (int) reminderId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return proximityIntent;
    }

}

