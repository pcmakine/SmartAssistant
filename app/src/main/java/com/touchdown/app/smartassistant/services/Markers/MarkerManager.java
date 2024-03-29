package com.touchdown.app.smartassistant.services.markers;

import android.graphics.Color;
import android.widget.LinearLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.touchdown.app.smartassistant.models.Task;
import com.touchdown.app.smartassistant.models.TriggerLocation;
import com.touchdown.app.smartassistant.data.asyncTasks.FetchAllLocationTasksTask;
import com.touchdown.app.smartassistant.data.asyncTasks.FetchTaskListListener;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Pete on 6.8.2014.
 */
public class MarkerManager implements FetchTaskListListener {
    private static final float SELECTED_COLOR = BitmapDescriptorFactory.HUE_AZURE;
    private static final float NON_EMPTY_MARKER_ON = BitmapDescriptorFactory.HUE_ORANGE;
    private static final float NON_EMPTY_MARKER_OFF = BitmapDescriptorFactory.HUE_VIOLET;
    private static final float EMPTY_MARKER_COLOR = BitmapDescriptorFactory.HUE_RED;

    private GoogleMap map;
    private Marker selectedMarker;

    private Map<Marker, MarkerData> markerDataMap;

    public MarkerManager(GoogleMap map) {
        this.map = map;
        this.markerDataMap = new HashMap<Marker, MarkerData>();
        updateMarkerData();
        this.selectedMarker = null;
    }

    public void updateMarkerData(){
        new FetchAllLocationTasksTask(this, false).execute();//taskManager.getAllTasksWithLocationTrigger();
    }

    private void putTaskMarkersOnMap(List<Task> taskList){
        for (Task task: taskList){
            TriggerLocation location = (TriggerLocation) task.getTrigger();
            saveMarker(generateMarker(task.getName(), location.getLatLng(), NON_EMPTY_MARKER_ON), task);
        }
        updateMarkerColors();
    }

    public Marker generateMarker(String text, LatLng loc, float color){
        Marker marker = (map.addMarker(new MarkerOptions()
                .position(loc)
                .title(StringUtils.abbreviate(text, 20))
                .icon(BitmapDescriptorFactory.defaultMarker(color))
                .draggable(true)));
        return marker;
    }

    public void saveMarker(Marker marker, Task task){    //reminder may be null
        Circle radius = null;
        if(task != null){
            TriggerLocation location = (TriggerLocation) task.getTrigger();
            radius = addRadius(location);
        }
        markerDataMap.put(marker, new MarkerData(task, radius, marker));
    }

    public void showRadius(Marker marker){
        MarkerData data = markerDataMap.get(marker);
        data.showRadius();
    }

    private Circle addRadius(TriggerLocation loc){
        int radius;
        radius = loc.getRadius();

        CircleOptions circleOptions = new CircleOptions()
                .center(loc.getLatLng())   //set center
                .radius(radius)
                .fillColor(Color.argb(50, 20, 134, 255))  //default
                .strokeColor(Color.BLUE)
                .strokeWidth(5);
        Circle circle = map.addCircle(circleOptions);
        return circle;
    }

    public void updateMarkerColors(){
        Set<Marker> markersOnMap = markerDataMap.keySet();
        for(Marker marker: markersOnMap){
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(getMarkerColor(marker)));
        }
    }

    private float getMarkerColor(Marker marker){
        if(marker.equals(selectedMarker)){
            return SELECTED_COLOR;
        }
        else if(markerDataMap.get(marker).noTask()){
            return EMPTY_MARKER_COLOR;
        }else if(markerDataMap.get(marker).hasActiveTask()){
            return NON_EMPTY_MARKER_ON;
        }else {
            return NON_EMPTY_MARKER_OFF;
        }
    }

    public void selectMarker(Marker marker){
        selectedMarker = marker;
        updateMarkerColors();
        showMarkerInfoWindow(marker);
    }

    public void unSelectMarker(){
        selectedMarker = null;
        updateMarkerColors();
    }

    private void showMarkerInfoWindow(Marker marker){
        Task task = markerDataMap.get(marker).getTask();
        if(task != null){
            TriggerLocation location = (TriggerLocation) task.getTrigger();
            if(location != null){
                marker.setTitle(StringUtils.abbreviate(task.getName(), 20));
                marker.showInfoWindow();
            }
        }
    }

    public void hideRadius(Marker marker){
        markerDataMap.get(marker).hideRadius();
    }

    public boolean removeSelectedIfEmpty(){
        if(selectedMarker != null){
            MarkerData data = markerDataMap.get(selectedMarker);
            if(data != null && data.getTask() == null){
                selectedMarker.remove();
                markerDataMap.remove(selectedMarker);
                return true;
            }
        }
        return false;
    }

    public void removeSelectedMarker(){
        MarkerData data = markerDataMap.get(selectedMarker);
        markerDataMap.remove(selectedMarker); //todo what happens if the program stops here, before the next command
        data.remove(selectedMarker);
        selectedMarker = null;
    }

    public void updateRadiusLocation(Marker marker){
        MarkerData data = markerDataMap.get(marker);

        data.removeRadius();
        data.setRadius(addRadius(data.getLocation()));
    }

    public Task getTask(Marker marker){
        if(marker != null){
            return markerDataMap.get(marker).getTask();
        }
        return null;
    }

    public Marker getSelectedMarker(){
        return selectedMarker;
    }

    public Marker getMarkerFromRadiusClick(LatLng position){
        Set<Map.Entry<Marker, MarkerData>> markerDataSet = markerDataMap.entrySet();
        for(Map.Entry<Marker, MarkerData> data: markerDataSet){
            MarkerData mData = data.getValue();
            Circle radius = mData.getRadius();
            LatLng center = radius.getCenter();
            float[] distance = new float[1];
            android.location.Location.distanceBetween(center.latitude, center.longitude, position.latitude, position.longitude, distance);
            if(distance[0] < radius.getRadius()){
                return mData.getMarker();
            }
        }
        return null;
    }

    public boolean userHasSelectedMarker(){
        return selectedMarker != null;
    }

    public boolean userHasSelectedEmptyMarker(){
        return userHasSelectedMarker() && markerDataMap.get(selectedMarker).getTask() == null;
    }

    public static float getSelectedColor(){
        return SELECTED_COLOR;
    }

    @Override
    public void updateTasks(List<Task> taskList) {
        map.clear();

        markerDataMap.clear();

        putTaskMarkersOnMap(taskList);
    }

    @Override
    public LinearLayout getOnProgressIndicator() {
        return null;
    }
}