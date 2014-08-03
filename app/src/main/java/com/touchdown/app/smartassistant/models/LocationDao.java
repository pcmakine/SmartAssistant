package com.touchdown.app.smartassistant.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;
import com.touchdown.app.smartassistant.data.Dao;
import com.touchdown.app.smartassistant.data.DbContract;

/**
 * Created by Pete on 3.8.2014.
 */
public class LocationDao implements Dao {
    private LatLng location;
    private long id;
    private long reminderId;

    public LocationDao(){}

    public LocationDao(long id, long reminderId, LatLng location){
        this.location = location;
    }

    public void setReminderId(long reminderId){
        this.reminderId = reminderId;
    }

    public long insert(SQLiteOpenHelper dbHelper){
        if(coordinatesLegal() && reminderId != -1){
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues vals = new ContentValues();
            vals.put(DbContract.LocationEntry.COLUMN_NAME_REMINDER_ID, reminderId);
            vals.put(DbContract.LocationEntry.COLUMN_NAME_LAT, location.latitude);
            vals.put(DbContract.LocationEntry.COLUMN_NAME_LONG, location.longitude);

            id = db.insert(DbContract.LocationEntry.TABLE_NAME, null, vals);
            return id;
        }
        return -1;
    }

    @Override
    public Dao getOne(SQLiteOpenHelper dbHelper, long reminderId) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbContract.LocationEntry.TABLE_NAME, null,
                DbContract.LocationEntry.COLUMN_NAME_REMINDER_ID + " = ?",
                new String[] {String.valueOf(id)}, null, null, null, null);

        if(cursor != null && cursor.moveToFirst()){
        }else{
            return null;
        }

        int idIndex = cursor.getColumnIndex(DbContract.LocationEntry._ID);

        int latIndex = cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_LAT);
        double lat = cursor.getDouble(latIndex);

        int longIndex = cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_LONG);
        double longitude = cursor.getDouble(longIndex);

        this.location = new LatLng(lat, longitude);
        this.reminderId = reminderId;
        this.id = cursor.getLong(idIndex);

        return this;
    }

    @Override
    public int remove(SQLiteOpenHelper dbHelper, long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(DbContract.LocationEntry.TABLE_NAME, DbContract.LocationEntry._ID + " =?",
                new String[] {id+""});
        db.close();
        return rowsAffected;
    }

    public Cursor getAll(SQLiteOpenHelper dbHelper){
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

        int idIndex = cursor.getColumnIndex(DbContract.LocationEntry._ID);
        long id = cursor.getLong(idIndex);

        int latIndex = cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_LAT);
        double lat = cursor.getDouble(latIndex);

        int longIndex = cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_LONG);
        double longitude = cursor.getDouble(longIndex);

        return new LocationDao(id, reminderId, new LatLng(lat, longitude));
    }
}
