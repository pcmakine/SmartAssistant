package com.touchdown.app.smartassistant.models;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.touchdown.app.smartassistant.services.Common;
import com.touchdown.app.smartassistant.data.DbContract;

/**
 * Created by Pete on 18.8.2014.
 */
public class TriggerLocation extends Trigger implements Parcelable {
    private static final String TABLE_NAME = DbContract.LocationEntry.TABLE_NAME;
    private static final String ID_COLUMN = DbContract.LocationEntry._ID;
    private static final int NUMBER_OF_BOOLEANS = 3;    //number of boolean fields. Used to create the parcel

    public static final int DEFAULT_RADIUS = 200;
    public static final int TRIGGER_TYPE = 0;

    private LatLng latLng;
    private int radius;     //meters
    private boolean triggerWhenEntering;
    private boolean triggerWhenLeaving;
    private boolean pending;        //whether the task was started inside the location and is waiting to be activated once the user is outside

    public TriggerLocation(long id, LatLng loc, int radius, long actionId) {
        super(id, 0, actionId);
        this.latLng = loc;

        if(radius == 0){
            this.radius = DEFAULT_RADIUS;
        }else{
            this.radius = radius;
        }
        this.triggerWhenEntering = true;
    }


    public static TriggerLocation createDefault(LatLng latLng){
        TriggerLocation loc = new TriggerLocation(-1, latLng, TriggerLocation.DEFAULT_RADIUS, -1);
        loc.setArrivalTrigger(true);
        return loc;
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

    public void setPending(boolean pending){
        this.pending = pending;
    }

    public boolean isPending(){
        return pending;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getIdColumn() {
        return ID_COLUMN;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues vals = new ContentValues();
        vals.put(DbContract.LocationEntry.COLUMN_NAME_LAT, latLng.latitude);
        vals.put(DbContract.LocationEntry.COLUMN_NAME_LONG, latLng.longitude);
        vals.put(DbContract.LocationEntry.COLUMN_NAME_RADIUS, radius);
        vals.put(DbContract.LocationEntry.COLUMN_NAME_TRIGGER_TYPE, this.getType());
        vals.put(DbContract.LocationEntry.COLUMN_NAME_TASK_ID, this.getTaskId());

        int arrivalTriggerInt = Common.booleanAsInt(triggerWhenEntering);
        int departureTriggerInt = Common.booleanAsInt(triggerWhenLeaving);
        int pendingInt = Common.booleanAsInt(pending);

        vals.put(DbContract.LocationEntry.COLUMN_NAME_TRIGGER_ON_ARRIVAL, arrivalTriggerInt);
        vals.put(DbContract.LocationEntry.COLUMN_NAME_TRIGGER_ON_DEPARTURE, departureTriggerInt);
        vals.put(DbContract.LocationEntry.COLUMN_NAME_PENDING, pendingInt);

        return vals;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //values from the superclass
        dest.writeLong(getId());
        dest.writeLong(getTaskId());
        dest.writeInt(getType());

        //this class' values
        dest.writeParcelable(latLng, 0);
        dest.writeInt(radius);
        dest.writeBooleanArray(
                new boolean[]{
                triggerWhenEntering,
                triggerWhenLeaving,
                pending});
    }

    public static final Parcelable.Creator<TriggerLocation> CREATOR
            = new Parcelable.Creator<TriggerLocation>() {
        public TriggerLocation createFromParcel(Parcel in) {
            return new TriggerLocation(in);
        }

        public TriggerLocation[] newArray(int size) {
            return new TriggerLocation[size];
        }
    };

    private TriggerLocation(Parcel in) {
        //superclass
        setId(in.readLong());
        setTaskId(in.readLong());
        setType(in.readInt());

        //this class
        latLng = in.readParcelable(LatLng.class.getClassLoader());
        radius = in.readInt();

        boolean[] booleans = new boolean[NUMBER_OF_BOOLEANS];
        in.readBooleanArray(booleans);
        triggerWhenEntering = booleans[0];
        triggerWhenLeaving = booleans[1];
        pending = booleans[2];
    }
}
