package com.touchdown.app.smartassistant.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.touchdown.app.smartassistant.data.DbContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Pete on 3.8.2014.
 */
public class ReminderDao {

    private String content;
    private long id;
    private LocationDao location;
    private boolean isOn;

    public ReminderDao(long id, String content, LocationDao location){
        this.content = content;
        this.id = id;       //remember that id may be -1 and location may be null
        this.location = location;
    }

    public boolean isOn(){
        return true;    //todo save info in db whether the reminder is on or not and add the field to this class
    }

    public void turnOn(){
        this.isOn = true;
    }

    public void turnOff(){
        this.isOn = false;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public LocationDao getLocation() {
        return location;
    }

    public long insert(SQLiteOpenHelper dbHelper){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(content != null && !content.equals("")){
            ContentValues vals = new ContentValues();
            vals.put(DbContract.ReminderEntry.COLUMN_CONTENT, content);

            id = db.insert(DbContract.ReminderEntry.TABLE_NAME, null, vals);
            if(location != null){
                location.setReminderId(id);
                location.insert(dbHelper);
            }
            db.close();
            return id;
        }
        return -1;
    }

    public static Cursor getAll(SQLiteOpenHelper dbHelper){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sortOrder = DbContract.ReminderEntry._ID + " ASC";

        Cursor c = db.query(
                DbContract.ReminderEntry.TABLE_NAME,
                null, //reads all the fields
                null,
                null,
                null,
                null,
                sortOrder);
        //todo can the db be closed at this point?
        return c;
    }

    public boolean update(SQLiteOpenHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues vals = values();
        int numOfRowsAffected = db.update(DbContract.ReminderEntry.TABLE_NAME,
                vals,
                DbContract.ReminderEntry._ID + " = ?",
                new String[] {String.valueOf(this.id)});

        if(this.location != null){
            this.location.update(dbHelper);
        }
        return numOfRowsAffected > 0;
    }

    public ContentValues values(){
        ContentValues vals = new ContentValues();
        vals.put(DbContract.ReminderEntry.COLUMN_CONTENT, this.content);

        return vals;
    }



    public static ReminderDao getOne(SQLiteOpenHelper dbHelper, long id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbContract.ReminderEntry.TABLE_NAME, null,
                DbContract.ReminderEntry._ID + " = ?",
                new String[] {String.valueOf(id)}, null, null, null, null);
        if(cursor != null && cursor.moveToFirst()){
        }else{
            return null;
        }

        int contentIndex = cursor.getColumnIndex(DbContract.ReminderEntry.COLUMN_CONTENT);
        String content = cursor.getString(contentIndex);

        LocationDao location = LocationDao.getReminderLocation(db, id);

        ReminderDao reminder = new ReminderDao(id, content, location);
        return reminder;
    }

    public static int remove(SQLiteOpenHelper dbHelper, long id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(DbContract.ReminderEntry.TABLE_NAME, DbContract.ReminderEntry._ID + " =?",
                new String[] {id+""});
        db.close();
        return rowsAffected;
    }

    public static List<ReminderDao> cursorDataAsList(SQLiteOpenHelper dbHelper, Cursor cursor){
        HashMap<Long, LocationDao> reminderIdLocationMap = LocationDao.cursorDataAsMap(LocationDao.getAll(dbHelper));
        List<ReminderDao> list = new ArrayList<ReminderDao>();
        if(cursor != null){
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                list.add(constructReminderDaoFromData(cursor, reminderIdLocationMap));
                cursor.moveToNext();
            }
        }
        return list;
    }


    private static ReminderDao constructReminderDaoFromData(Cursor cursor, HashMap<Long, LocationDao> reminderIdLocationMap){
        Long id = cursor.getLong(cursor.getColumnIndex(DbContract.ReminderEntry._ID));
        String content = cursor.getString(cursor.getColumnIndex(DbContract.ReminderEntry.COLUMN_CONTENT));
        LocationDao loc = reminderIdLocationMap.get(id);

        return new ReminderDao(id, content, loc);
    }

    public long getId(){
        return id;
    }
}
