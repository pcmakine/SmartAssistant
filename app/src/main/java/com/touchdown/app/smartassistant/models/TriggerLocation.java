package com.touchdown.app.smartassistant.models;

import android.content.ContentValues;

import com.google.android.gms.maps.model.LatLng;
import com.touchdown.app.smartassistant.Util;
import com.touchdown.app.smartassistant.data.DbContract;
import com.touchdown.app.smartassistant.models.Trigger;

/**
 * Created by Pete on 18.8.2014.
 */
public class TriggerLocation extends Trigger {
    private static final String TABLE_NAME = DbContract.LocationEntry.TABLE_NAME;
    private static final String ID_COLUMN = DbContract.LocationEntry._ID;

    public static final int DEFAULT_RADIUS = 100;
    public static final int TRIGGER_TYPE = 0;

    private LatLng latLng;
    private int radius;     //meters
    private boolean triggerWhenEntering;
    private boolean triggerWhenLeaving;

    public TriggerLocation(long id, LatLng loc, int radius, long actionId) {
        super(id, 0, actionId);
        this.latLng = loc;

        if(radius == 0){
            this.radius = DEFAULT_RADIUS;
        }else{
            this.radius = radius;
        }
        setTableName(TABLE_NAME);
        setIdColumn(ID_COLUMN);
        this.triggerWhenEntering = true;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void turnOnArrivalTrigger(){
        this.triggerWhenEntering = true;
    }

    public void turnOffArrivalTrigger(){
        this.triggerWhenEntering = false;
    }

    public void turnOnDepartureTrigger(){
        this.triggerWhenLeaving = true;
    }

    public void turnOffDepartureTrigger(){
        this.triggerWhenLeaving = false;
    }

    public boolean isArrivalTriggerOn(){
        return triggerWhenEntering;
    }

    public boolean isDepartureTriggerOn(){
        return triggerWhenLeaving;
    }

    public void setArrivalTrigger(boolean triggerOn){
        this.triggerWhenEntering = triggerOn;
    }

    public void setDepartureTrigger(boolean triggerOn){
        this.triggerWhenLeaving = triggerOn;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues vals = new ContentValues();
        vals.put(DbContract.LocationEntry.COLUMN_NAME_LAT, latLng.latitude);
        vals.put(DbContract.LocationEntry.COLUMN_NAME_LONG, latLng.longitude);
        vals.put(DbContract.LocationEntry.COLUMN_NAME_RADIUS, radius);
        vals.put(DbContract.LocationEntry.COLUMN_NAME_TRIGGER_TYPE, this.getType());
        vals.put(DbContract.LocationEntry.COLUMN_NAME_TASK_ID, this.getActionId());

        int arrivalTriggerInt = Util.booleanAsInt(triggerWhenEntering);
        int departureTriggerInt = Util.booleanAsInt(triggerWhenLeaving);

        vals.put(DbContract.LocationEntry.COLUMN_NAME_TRIGGER_ON_ARRIVAL, arrivalTriggerInt);
        vals.put(DbContract.LocationEntry.COLUMN_NAME_TRIGGER_ON_DEPARTURE, departureTriggerInt);
        return vals;
    }
}
