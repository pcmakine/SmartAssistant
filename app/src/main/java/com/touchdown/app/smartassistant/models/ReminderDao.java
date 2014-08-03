package com.touchdown.app.smartassistant.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import com.touchdown.app.smartassistant.data.DbContract;

/**
 * Created by Pete on 3.8.2014.
 */
public class ReminderDao {

    private String content;
    private long id;
    private LocationDao location;

    public String getContent() {
        return content;
    }

    public LocationDao getLocation() {
        return location;
    }

    public ReminderDao(long id, String content, LocationDao location){
        this.content = content;
        this.id = id;       //remember that id may be -1 and location may be null
        this.location = location;
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
        String sortOrder = DbContract.ReminderEntry._ID + " DESC";

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

    public long getId(){
        return id;
    }
}
