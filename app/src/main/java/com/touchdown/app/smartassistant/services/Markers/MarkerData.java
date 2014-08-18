package com.touchdown.app.smartassistant.services.Markers;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;
import com.touchdown.app.smartassistant.models.ReminderLocation;
import com.touchdown.app.smartassistant.models.Reminder;

/**
 * Created by Pete on 8.8.2014.
 */
public class MarkerData implements Comparable{
    private Reminder reminder;
    private Circle radius;
    private Marker marker;

    public MarkerData(Reminder reminder, Circle radius, Marker marker) {
        this.radius = radius;
        this.reminder = reminder;
        this.marker = marker;
    }

    public Marker getMarker(){
        return marker;
    }

    public Circle getRadius() {
        return radius;
    }

    public void hideRadius(){
        if(radius != null){
            radius.setVisible(false);
        }
    }

    public void showRadius(){
        if(radius != null){
            radius.setVisible(true);
        }
    }

    public void setRadius(Circle radius){
        this.radius = radius;
    }

    public boolean noReminder(){
        return reminder == null;
    }

    public Reminder getReminder() {
        return reminder;
    }

    public ReminderLocation getLocation(){
        if(reminder != null){
            return reminder.getReminderLocation();
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

    @Override
    public int compareTo(Object another) {
        MarkerData compareData = (MarkerData) another;
        if(reminder == null && compareData.noReminder()){
            return 0;
        }else if(reminder == null){
            return 1;
        }else if(compareData.noReminder()){
            return -1;
        }else{
            return reminder.compareTo(compareData.getReminder());
        }
    }
}
