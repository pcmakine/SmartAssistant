package com.touchdown.app.smartassistant.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.touchdown.app.smartassistant.data.DbContract;
import com.touchdown.app.smartassistant.data.DbHelper;
import com.touchdown.app.smartassistant.models.LocationDao;
import com.touchdown.app.smartassistant.models.Reminder;
import com.touchdown.app.smartassistant.views.OnGoingNotification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;

/**
 * Created by Pete on 3.8.2014.
 */

/**
 * Class for persisting the reminders to database and doing related actions. Implemented as a singleton.
 */
public class ReminderManager extends Observable{
    private SQLiteOpenHelper dbHelper;
    private static ReminderManager sInstance;

    private ReminderManager(SQLiteOpenHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    public synchronized static ReminderManager getInstance(Context context){
        if(sInstance == null){
            sInstance = new ReminderManager(new DbHelper(context.getApplicationContext()));
        }
        return sInstance;
    }

    public long insert(Reminder reminder){
        long id = insertReminderInDatabase(reminder);
        insertRemindersLocationInDatabase(reminder);
        notifyDataObservers();

        if(reminder.isOn() && reminder.getLocation() != null){
            ProximityAlarmManager.saveAlert(reminder);
        }

        OnGoingNotification.updateNotification();

        return id;
    }

    private long insertReminderInDatabase(Reminder reminder){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(reminder.getContent() != null && !reminder.getContent().equals("")){
            ContentValues vals = values(reminder);

            reminder.setId(db.insert(DbContract.ReminderEntry.TABLE_NAME, null, vals));
            db.close();
            return reminder.getId();
        }
        return -1;
    }

    private void insertRemindersLocationInDatabase(Reminder reminder){
        LocationDao location = reminder.getLocation();
        if(location != null){
            location.setReminderId(reminder.getId());
            location.insert(dbHelper);
        }
    }

    public Cursor getAll(){
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

    public boolean update(Reminder reminder) {
        int numOfRowsAffected = updateReminderInDatabase(reminder);

        if(reminder.getLocation() != null){
            reminder.getLocation().update(dbHelper);
        }
        notifyDataObservers();

        updateProximityAlarm(reminder);

        OnGoingNotification.updateNotification();

        return numOfRowsAffected > 0;
    }

    private int updateReminderInDatabase(Reminder reminder){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues vals = values(reminder);
        int numOfRowsAffected = db.update(DbContract.ReminderEntry.TABLE_NAME,
                vals,
                DbContract.ReminderEntry._ID + " = ?",
                new String[] {String.valueOf(reminder.getId())});
        return numOfRowsAffected;
    }

    private void updateProximityAlarm(Reminder reminder){
        if(reminder.isOn() && reminder.getLocation() != null){
            ProximityAlarmManager.updateAlert(reminder);
        }else{
            ProximityAlarmManager.removeAlert(reminder.getId());
        }
    }

    public ContentValues values(Reminder reminder){
        ContentValues vals = new ContentValues();
        vals.put(DbContract.ReminderEntry.COLUMN_NAME_CONTENT, reminder.getContent());
        int onInteger = (reminder.isOn())? 1: 0;
        vals.put(DbContract.ReminderEntry.COLUMN_NAME_ON, onInteger);

        return vals;
    }

    public Reminder getOne(long id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbContract.ReminderEntry.TABLE_NAME, null,
                DbContract.ReminderEntry._ID + " = ?",
                new String[] {String.valueOf(id)}, null, null, null, null);
        if(cursor != null && cursor.moveToFirst()){
        }else{
            return null;
        }

        LocationDao location = LocationDao.getReminderLocation(db, id);
        return constructReminderFromData(cursor, location);
    }

    public int remove(long id){
        int rowsAffected = removeReminderFromDatabase(id);
        notifyDataObservers();

        ProximityAlarmManager.removeAlert(id);

        OnGoingNotification.updateNotification();

        return rowsAffected;
    }

    private int removeReminderFromDatabase(long id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(DbContract.ReminderEntry.TABLE_NAME, DbContract.ReminderEntry._ID + " =?",
                new String[] {id+""});
        db.close();
        return rowsAffected;
    }

    public List<Reminder> cursorDataAsList(Cursor cursor){
        HashMap<Long, LocationDao> reminderIdLocationMap = LocationDao.cursorDataAsMap(LocationDao.getAll(dbHelper));
        List<Reminder> list = new ArrayList<Reminder>();
        if(cursor != null){
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                list.add(constructReminderFromData(cursor, reminderIdLocationMap));
                cursor.moveToNext();
            }
        }
        return list;
    }

    public int getActiveReminderCount(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor mCount= db.rawQuery("select count(*) from " + DbContract.ReminderEntry.TABLE_NAME +
                " where " + DbContract.ReminderEntry.COLUMN_NAME_ON + " = ?", new String[] {"1"});
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();
        return count;
    }


    private Reminder constructReminderFromData(Cursor cursor, HashMap<Long, LocationDao> reminderIdLocationMap){
        Long id = cursor.getLong(cursor.getColumnIndex(DbContract.ReminderEntry._ID));

        return constructReminderFromData(cursor, reminderIdLocationMap.get(id));
    }

    private static Reminder constructReminderFromData(Cursor cursor, LocationDao location){
        Long id = cursor.getLong(cursor.getColumnIndex(DbContract.ReminderEntry._ID));
        String content = cursor.getString(cursor.getColumnIndex(DbContract.ReminderEntry.COLUMN_NAME_CONTENT));
        boolean reminderOn = (cursor.getInt(cursor.getColumnIndex(DbContract.ReminderEntry.COLUMN_NAME_ON)) == 1);

        Reminder reminder = new Reminder(id, content, location);

        reminder.setOn(reminderOn);

        return reminder;
    }

    private void notifyDataObservers(){
        setChanged();
        notifyObservers();
        clearChanged();
    }
}
