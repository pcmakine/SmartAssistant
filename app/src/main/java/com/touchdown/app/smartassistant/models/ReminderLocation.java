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

    public ContentValues values(){
        ContentValues vals = new ContentValues();
        vals.put(DbContract.LocationEntry.COLUMN_NAME_LAT, this.lat);
        vals.put(DbContract.LocationEntry.COLUMN_NAME_LONG, this.lng);
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

    public static ReminderLocation getReminderLocation(SQLiteDatabase db, long reminderId){
        Cursor cursor = db.query(DbContract.LocationEntry.TABLE_NAME, null,
                DbContract.LocationEntry.COLUMN_NAME_REMINDER_ID + " = ?",
                new String[] {String.valueOf(reminderId)}, null, null, null, null);

        if(cursor != null && cursor.moveToFirst()){
        }else{
            return null;
        }

        return constructLocationDaoFromData(cursor);
    }

    public static HashMap<Long, ReminderLocation> cursorDataAsMap(Cursor cursor){
        HashMap<Long, ReminderLocation> reminderIdLocationMap = new HashMap<Long, ReminderLocation>();

        if(cursor != null){
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                ReminderLocation loc = constructLocationDaoFromData(cursor);
                reminderIdLocationMap.put(loc.getReminderId(), loc);
                //todo is it needed to overwrite the hash and equals function in this case?
                cursor.moveToNext();
            }
        }
        return reminderIdLocationMap;
    }

    private static ReminderLocation constructLocationDaoFromData(Cursor cursor){
        long id = cursor.getLong(cursor.getColumnIndex(DbContract.LocationEntry._ID));
        long reminderId = cursor.getLong(cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_REMINDER_ID));
        double lat = cursor.getDouble(cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_LAT));
        double longitude = cursor.getDouble(cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_LONG));

        int radiusIndex = cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_RADIUS);
        int radius = cursor.getInt(radiusIndex);
        return new ReminderLocation(id, reminderId, new LatLng(lat, longitude), radius);
    }
}
