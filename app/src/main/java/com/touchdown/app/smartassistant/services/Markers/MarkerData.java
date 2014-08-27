package com.touchdown.app.smartassistant.services.Markers;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;
import com.touchdown.app.smartassistant.models.Task;
import com.touchdown.app.smartassistant.models.TriggerLocation;

/**
 * Created by Pete on 8.8.2014.
 */
public class MarkerData implements Comparable{
    private Task task;
    private Circle radius;
    private Marker marker;

    public MarkerData(Task task, Circle radius, Marker marker) {
        this.radius = radius;
        this.task = task;
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


    public boolean noTask(){
        return task == null;
    }

    public Task getTask() {
        return task;
    }

    public TriggerLocation getLocation(){
        if(task != null){
            return (TriggerLocation) task.getTrigger();
        }
        return null;
    }

    public boolean hasActiveTask(){
        if(task != null){
            return task.isActive();
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
        if(task == null && compareData.noTask()){
            return 0;
        }else if(task == null){
            return 1;
        }else if(compareData.noTask()){
            return -1;
        }else{
            return task.compareTo(compareData.getTask());
        }
    }
}
