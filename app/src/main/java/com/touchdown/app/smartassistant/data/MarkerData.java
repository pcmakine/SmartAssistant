package com.touchdown.app.smartassistant.data;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;
import com.touchdown.app.smartassistant.models.LocationDao;
import com.touchdown.app.smartassistant.models.Reminder;

/**
 * Created by Pete on 8.8.2014.
 */
public class MarkerData implements Comparable{
    private Reminder reminder;
    private Circle radius;

    public MarkerData(Reminder reminder, Circle radius) {
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

    public Reminder getReminder() {
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
