package com.touchdown.app.smartassistant.services;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.touchdown.app.smartassistant.data.DataOperation;
import com.touchdown.app.smartassistant.data.MarkerData;
import com.touchdown.app.smartassistant.models.LocationDao;
import com.touchdown.app.smartassistant.models.Reminder;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by Pete on 6.8.2014.
 */
public class MarkerManager {
    private static final float SELECTED_COLOR = BitmapDescriptorFactory.HUE_AZURE;
    private static final float NON_EMPTY_MARKER_ON = BitmapDescriptorFactory.HUE_ORANGE;
    private static final float NON_EMPTY_MARKER_OFF = BitmapDescriptorFactory.HUE_VIOLET;
    private static final float EMPTY_MARKER_COLOR = BitmapDescriptorFactory.HUE_RED;

    private GoogleMap map;
    //  private Map<Marker, ReminderDao> markerReminderMap;
    // private HashMap<LatLng, Circle> centerRadiusMap;
    private Marker selectedMarker;

    private Map<Marker, MarkerData> markerDataMap;

    private ReminderManager reminderManager;

    public MarkerManager(GoogleMap map, ReminderManager reminderManager){
        this.map = map;
        this.reminderManager = reminderManager;
        this.markerDataMap = new HashMap<Marker, MarkerData>();
        populateMapWithMarkers();
        this.selectedMarker = null;
    }

    public void updateMarkerData(){
        List<Reminder> reminderList = reminderManager.cursorDataAsList(reminderManager.getAll());

        map.clear();

        markerDataMap.clear();

        populateMapWithMarkers(reminderList);

    }

 /*   public void updateMarkerData(DataOperation op){
        List<Reminder> reminderList = reminderManager.cursorDataAsList(reminderManager.getAll());

        Map<Marker, MarkerData> data = MapSort.sortByValue(markerDataMap);
        Set<Marker> keyset = data.keySet();
        Iterator<Marker> iterator = keyset.iterator();


        for(Reminder reminder: reminderList){
            MarkerData markerData;
            if(iterator.hasNext()){
                markerData = iterator.next();
                if(reminder.equals(markerData.getReminder())){

                }
            }

        }
        this.selectedMarker = null;
    }

    private void updateMarkerDataOnRemove(Set<Marker> data, Iterator<Marker> iterator, List<Reminder> reminderList){

        while(iterator.hasNext()){
            if(){

            }
        }

        for(Reminder reminder: reminderList){
            MarkerData markerData;
            if(iterator.hasNext()){
                markerData = iterator.next();
                if(reminder.equals(markerData.getReminder())){

                }
            }

        }

    }*/

    public void populateMapWithMarkers(List<Reminder> reminderList){
        for (Reminder reminder: reminderList){
            if(reminder.getLocation() != null){
                saveMarker(generateMarker(reminder.getContent(), reminder.getLocation().getLatLng(), NON_EMPTY_MARKER_ON), reminder);
            }
        }
        updateMarkerColors();
    }

    public void populateMapWithMarkers(){
        List<Reminder> reminderList = reminderManager.cursorDataAsList(reminderManager.getAll());
        populateMapWithMarkers(reminderList);
    }

    public Marker generateMarker(String text, LatLng loc, float color){
        Marker marker = (map.addMarker(new MarkerOptions()
                .position(loc)
                .title(StringUtils.abbreviate(text, 20))
                .icon(BitmapDescriptorFactory.defaultMarker(color))
                .draggable(true)));
        return marker;
    }

    public void saveMarker(Marker marker, Reminder reminder){    //reminder may be null
        Circle radius = null;
        if(reminder != null){
            radius = addRadius(reminder.getLocation());
        }
        markerDataMap.put(marker, new MarkerData(reminder, radius));

    }

    public void updateRadius(Marker marker){
        MarkerData data = markerDataMap.get(marker);
        if(data.getReminder() != null &&
                data.getReminder().getLocation() != null){
            Circle radius = addRadius(data.getReminder().getLocation());
            data.setRadius(radius);
        }
    }

    private Circle addRadius(LocationDao loc){
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
        else if(markerDataMap.get(marker).noReminder()){
            return EMPTY_MARKER_COLOR;
        }else if(markerDataMap.get(marker).hasActiveReminder()){
            return NON_EMPTY_MARKER_ON;
        }else {
            return NON_EMPTY_MARKER_OFF;
        }
    }

    public void selectMarker(Marker marker){
        selectedMarker = marker;
        int markerHash = marker.hashCode();
        Marker mapMarker = markerDataMap.keySet().iterator().next();
        int mapMarkerHash = mapMarker.hashCode();
        int selectedHash = selectedMarker.hashCode();
        updateMarkerColors();
        //removeRadiusFromMap();
        showMarkerInfoWindow(marker);
    }

    public void unSelectMarker(){
        selectedMarker = null;
        updateMarkerColors();
        //removeRadiusFromMap();
    }

    private void showMarkerInfoWindow(Marker marker){
        Reminder reminder = markerDataMap.get(marker).getReminder();
        if(reminder != null && reminder.getLocation() != null){
            marker.setTitle(StringUtils.abbreviate(reminder.getContent(), 20));
            marker.showInfoWindow();
        }
    }

    public void removeRadius(Marker marker){
        markerDataMap.get(marker).removeRadius();
    }

    public boolean removeSelectedIfEmpty(){
        if(markerDataMap.get(selectedMarker).getReminder() == null){
            selectedMarker.remove();
            markerDataMap.remove(selectedMarker);
            return true;
        }
        return false;
    }

    public void removeSelectedMarker(){
        MarkerData data = markerDataMap.get(selectedMarker);
        markerDataMap.remove(selectedMarker); //todo what happens if the program stops here, before the next command
        data.remove(selectedMarker);
        selectedMarker = null;

    }

    public Reminder getReminder(Marker marker){
        if(marker != null){
            return markerDataMap.get(marker).getReminder();
        }
        return null;
    }

    public Marker getSelectedMarker(){
        return selectedMarker;
    }

    public boolean userHasSelectedMarker(){
        return selectedMarker != null;
    }

    public boolean userHasSelectedEmptyMarker(){
        return userHasSelectedMarker() && markerDataMap.get(selectedMarker).getReminder() == null;
    }

    public static float getSelectedColor(){
        return SELECTED_COLOR;
    }

}