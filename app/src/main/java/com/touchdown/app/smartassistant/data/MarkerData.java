package com.touchdown.app.smartassistant.data;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.CircleOptionsCreator;
import com.google.android.gms.maps.model.Marker;
import com.touchdown.app.smartassistant.models.LocationDao;
import com.touchdown.app.smartassistant.models.ReminderDao;

/**
 * Created by Pete on 8.8.2014.
 */
public class MarkerData {
    private ReminderDao reminder;
    private Circle radius;

    public MarkerData(ReminderDao reminder, Circle radius) {
        this.radius = radius;
        this.reminder = reminder;
    }

    public Circle getRadius() {
        return radius;
    }

    public void setRadius(Circle radius){
        this.radius = radius;
    }

    public boolean noReminder(){
        return reminder == null;
    }

    public ReminderDao getReminder() {
        return reminder;
    }

    public LocationDao getLocation(){
        if(reminder != null){
            return reminder.getLocation();
        }
        return null;
    }

    public boolean hasActiveReminder(){
        if(reminder != null){
            return reminder.isOn();
        }
        return false;
    }

    public void removeRadius(){
        radius.remove();
    }

    public void remove(Marker marker){
        radius.remove();
        marker.remove();
    }
}
