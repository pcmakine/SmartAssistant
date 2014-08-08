package com.touchdown.app.smartassistant.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;
import com.touchdown.app.smartassistant.data.DbContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Pete on 3.8.2014.
 */
public class LocationDao {
    public static final int DEFAULT_RADIUS = 100;

    private LatLng latLng;
    private long id;
    private long reminderId;
    private int radius;     //meters


    public LocationDao(long id, long reminderId, LatLng location, int radius){
        this.latLng = location;
        this.id = id;
        this.reminderId = reminderId;
        if(radius == 0){
            this.radius = DEFAULT_RADIUS;
        }else{
            this.radius = radius;
        }
    }

    public LatLng getLatLng() {
        return latLng;
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

    public void setReminderId(long reminderId){
        this.reminderId = reminderId;
    }

    public void setLocation(LatLng location) {
        this.latLng = location;
    }

    public ContentValues values(){
        ContentValues vals = new ContentValues();
        vals.put(DbContract.LocationEntry.COLUMN_NAME_LAT, this.latLng.latitude);
        vals.put(DbContract.LocationEntry.COLUMN_NAME_LONG, this.latLng.longitude);
        vals.put(DbContract.LocationEntry.COLUMN_NAME_REMINDER_ID, this.reminderId);
        vals.put(DbContract.LocationEntry.COLUMN_NAME_RADIUS, this.radius);

        return vals;
    }

    public boolean update(SQLiteOpenHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues vals = values();
        int numOfRowsAffected = db.update(DbContract.LocationEntry.TABLE_NAME,
                vals,
                DbContract.LocationEntry._ID + " = ?",
                new String[] {String.valueOf(this.id)});
        return numOfRowsAffected > 0;
    }

    public long insert(SQLiteOpenHelper dbHelper){
        if(coordinatesLegal() && reminderId != -1){
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues vals = values();

            id = db.insert(DbContract.LocationEntry.TABLE_NAME, null, vals);
            return id;
        }
        return -1;
    }

/*    public int remove(SQLiteOpenHelper dbHelper, long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(DbContract.LocationEntry.TABLE_NAME, DbContract.LocationEntry._ID + " =?",
                new String[] {id+""});
        db.close();
        return rowsAffected;
    }*/

    public static Cursor getAll(SQLiteOpenHelper dbHelper){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sortOrder = DbContract.LocationEntry._ID + " DESC";

        Cursor c = db.query(
                DbContract.LocationEntry.TABLE_NAME,
                null, //reads all the fields
                null,
                null,
                null,
                null,
                sortOrder);
        //todo can the db be closed at this point?
        return c;
    }

    private boolean coordinatesLegal(){
        //todo check that the lat and long coords exist
        return true;
    }

    public static LocationDao getReminderLocation(SQLiteDatabase db, long reminderId){
        Cursor cursor = db.query(DbContract.LocationEntry.TABLE_NAME, null,
                DbContract.LocationEntry.COLUMN_NAME_REMINDER_ID + " = ?",
                new String[] {String.valueOf(reminderId)}, null, null, null, null);

        if(cursor != null && cursor.moveToFirst()){
        }else{
            return null;
        }

        return constructLocationDaoFromData(cursor);
    }

    public static HashMap<Long, LocationDao> cursorDataAsMap(Cursor cursor){
        HashMap<Long, LocationDao> reminderIdLocationMap = new HashMap<Long, LocationDao>();

        if(cursor != null){
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                LocationDao loc = constructLocationDaoFromData(cursor);
                reminderIdLocationMap.put(loc.getReminderId(), loc);
                //todo is it needed to overwrite the hash and equals function in this case?
                cursor.moveToNext();
            }
        }
        return reminderIdLocationMap;
    }

    private static LocationDao constructLocationDaoFromData(Cursor cursor){
        long id = cursor.getLong(cursor.getColumnIndex(DbContract.LocationEntry._ID));
        long reminderId = cursor.getLong(cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_REMINDER_ID));
        double lat = cursor.getDouble(cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_LAT));
        double longitude = cursor.getDouble(cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_LONG));

        int radiusIndex = cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_RADIUS);
        int radius = cursor.getInt(radiusIndex);
        return new LocationDao(id, reminderId, new LatLng(lat, longitude), radius);
    }
}
