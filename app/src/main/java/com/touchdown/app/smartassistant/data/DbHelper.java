package com.touchdown.app.smartassistant.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.touchdown.app.smartassistant.data.DbContract.*;

/**
 * Created by Pete on 3.8.2014.
 */
public class DbHelper extends SQLiteOpenHelper{
    public static final String LOG_TAG = DbHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "smartassist.db";

    public DbHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_REMINDER_TABLE = "CREATE TABLE " + ReminderEntry.TABLE_NAME
                + " (" + ReminderEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                ReminderEntry.COLUMN_NAME_CONTENT + " TEXT NOT NULL, " +
                ReminderEntry.COLUMN_NAME_ON + " INTEGER NOT NULL)";

        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + LocationEntry.TABLE_NAME +
                " (" + LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                LocationEntry.COLUMN_NAME_TRIGGER_TYPE + " INTEGER NOT NULL, " +
                LocationEntry.COLUMN_NAME_LAT + " REAL NOT NULL, " +
                LocationEntry.COLUMN_NAME_LONG + " REAL NOT NULL, " +
                LocationEntry.COLUMN_NAME_RADIUS + " INTEGER NOT NULL, " +
                LocationEntry.COLUMN_NAME_PARENT_ID + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + LocationEntry.COLUMN_NAME_PARENT_ID + ") REFERENCES " +
                TriggerEntry.TABLE_NAME + "(" + TriggerEntry._ID + ") " +
                " ON DELETE CASCADE)";

        final String SQL_CREATE_ACTION_TRIGGER_TABLE = "CREATE TABLE " + ActionTriggerEntry.TABLE_NAME +
                " (" + ActionTriggerEntry.COLUMN_NAME_ACTION_ID + " INTEGER NOT NULL, " +
                ActionTriggerEntry.COLUMN_NAME_TRIGGER_ID + " INTEGER NOT NULL, " +
                " PRIMARY KEY (" + ActionTriggerEntry.COLUMN_NAME_ACTION_ID + ", " +
                ActionTriggerEntry.COLUMN_NAME_TRIGGER_ID + ")" +
                " FOREIGN KEY (" + ActionTriggerEntry.COLUMN_NAME_ACTION_ID + ") REFERENCES "
                + ActionEntry.TABLE_NAME + "(" + ActionEntry._ID +")" +
                " FOREIGN KEY (" + ActionTriggerEntry.COLUMN_NAME_TRIGGER_ID + ") REFERENCES "
                + TriggerEntry.TABLE_NAME + "(" + TriggerEntry._ID + ") " +
                "ON DELETE CASCADE)";

        final String SQL_CREATE_TRIGGER_TABLE = "CREATE TABLE " + TriggerEntry.TABLE_NAME +
                " (" + TriggerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                TriggerEntry.COLUMN_NAME_TRIGGER_TYPE + " INTEGER NOT NULL)";

        final String SQL_CREATE_ACTION_TABLE = "CREATE TABLE " + ActionEntry.TABLE_NAME +
                " (" + ActionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                ActionEntry.COLUMN_NAME_ACTION_NAME + " TEXT NOT NULL, " +
                ActionEntry.COLUMN_NAME_ACTION_TYPE + " INTEGER NOT NULL)";

/*        final String SQL_CREATE_TIME_TABLE = "CREATE TABLE " + DbContract.TimeEntry.TABLE_NAME +
                " (" + DbContract.TimeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                TimeEntry.COLUMN_NAME_REMINDER_ID + " INTEGER, " +
                TimeEntry.COLUMN_NAME_DATE + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + TimeEntry.COLUMN_NAME_REMINDER_ID + ") REFERENCES "
                + ReminderEntry.TABLE_NAME + "(" + ReminderEntry._ID +
                ") ON DELETE CASCADE)";*/

       // db.execSQL(SQL_CREATE_REMINDER_TABLE);
       // db.execSQL(SQL_CREATE_LOCATION_TABLE);

        db.execSQL(SQL_CREATE_TRIGGER_TABLE);
        db.execSQL(SQL_CREATE_LOCATION_TABLE);
        db.execSQL(SQL_CREATE_ACTION_TABLE);
        db.execSQL(SQL_CREATE_ACTION_TRIGGER_TABLE);

        Log.d(LOG_TAG, SQL_CREATE_TRIGGER_TABLE);
        Log.d(LOG_TAG, SQL_CREATE_LOCATION_TABLE);
        Log.d(LOG_TAG, SQL_CREATE_ACTION_TRIGGER_TABLE);
        Log.d(LOG_TAG, SQL_CREATE_REMINDER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + DbContract.ReminderEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS" + DbContract.LocationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS" + DbContract.TimeEntry.TABLE_NAME);
        onCreate(db);
    }
}
