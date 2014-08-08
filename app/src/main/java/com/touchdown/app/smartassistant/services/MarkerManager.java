package com.touchdown.app.smartassistant.services;

import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.touchdown.app.smartassistant.data.DbHelper;
import com.touchdown.app.smartassistant.data.MarkerData;
import com.touchdown.app.smartassistant.models.LocationDao;
import com.touchdown.app.smartassistant.models.ReminderDao;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

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

    private HashMap<Marker, MarkerData> markerDataMap;

    public MarkerManager(GoogleMap map, Context context){
        this.map = map;
      //  this.markerReminderMap = new HashMap();
        this.markerDataMap = new HashMap<Marker, MarkerData>();
        populateMapWithMarkers(context);
        this.selectedMarker = null;
    }

    public void populateMapWithMarkers(Context context){
        DbHelper dbHelper = new DbHelper(context);
        List<ReminderDao> reminderList = ReminderDao.cursorDataAsList(dbHelper, ReminderDao.getAll(dbHelper));
        for (ReminderDao reminder: reminderList){
            if(reminder.getLocation() != null){
                saveMarker(generateMarker(reminder.getContent(), reminder.getLocation().getLatLng(), NON_EMPTY_MARKER_ON), reminder);
            }
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

    public void saveMarker(Marker marker, ReminderDao reminder){    //reminder may be null
        Circle radius = null;
        if(reminder != null){
            radius = addRadius(reminder.getLocation());
        }
        markerDataMap.put(marker, new MarkerData(reminder, radius));

       // markerReminderMap.put(marker, reminder);
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
        //   removeRadiusFromMap();
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
        ReminderDao reminder = markerDataMap.get(marker).getReminder();
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

    public ReminderDao getReminder(Marker marker){
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