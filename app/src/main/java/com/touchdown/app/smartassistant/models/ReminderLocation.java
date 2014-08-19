package com.touchdown.app.smartassistant.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;
import com.touchdown.app.smartassistant.data.DbContract;

import java.util.HashMap;

/**
 * Created by Pete on 3.8.2014.
 */
public class ReminderLocation {
    private long id;
    private long reminderId;
    private double lat;
    private double lng;
    private int radius;     //meters

    public static final int DEFAULT_RADIUS = 100;

    public ReminderLocation(long id, long reminderId, LatLng location, int radius){
        this.reminderId = reminderId;
        this.id = id;
        this.lat = location.latitude;
        this.lng = location.longitude;

        if(radius == 0){
            this.radius = DEFAULT_RADIUS;
        }else{
            this.radius = radius;
        }
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }

    public long getId() {
        return id;
    }

    public long getReminderId() {
        return reminderId;
    }

    public int getRadius(){
        return radius;
    }

    public void setRadius(int radius){
        this.radius = radius;
    }

    public void setReminderId(long reminderId){
        this.reminderId = reminderId;
    }

    public void setLocation(LatLng location) {
        this.lat = location.latitude;
        this.lng = location.longitude;
    }

    private boolean coordinatesLegal(){
        //todo check that the lat and long coords exist
        return true;
    }
}
