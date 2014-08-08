package com.touchdown.app.smartassistant.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Pete on 3.8.2014.
 */
public class DbHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "smartassist.db";

    public DbHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_REMINDER_TABLE = "CREATE TABLE " + DbContract.ReminderEntry.TABLE_NAME
                + " (" + DbContract.ReminderEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                DbContract.ReminderEntry.COLUMN_NAME_CONTENT + " TEXT NOT NULL, " +
                DbContract.ReminderEntry.COLUMN_NAME_ON + " INTEGER NOT NULL)";

        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + DbContract.LocationEntry.TABLE_NAME +
                " (" + DbContract.LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                DbContract.LocationEntry.COLUMN_NAME_REMINDER_ID + " INTEGER, " +
                DbContract.LocationEntry.COLUMN_NAME_LAT + " REAL NOT NULL, " +
                DbContract.LocationEntry.COLUMN_NAME_LONG + " REAL NOT NULL, " +
                DbContract.LocationEntry.COLUMN_NAME_RADIUS + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + DbContract.LocationEntry.COLUMN_NAME_REMINDER_ID + ") REFERENCES "
                + DbContract.ReminderEntry.TABLE_NAME + "(" + DbContract.ReminderEntry._ID +
                ") ON DELETE CASCADE)";

        final String SQL_CREATE_TIME_TABLE = "CREATE TABLE " + DbContract.TimeEntry.TABLE_NAME +
                " (" + DbContract.TimeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                DbContract.TimeEntry.COLUMN_NAME_REMINDER_ID + " INTEGER, " +
                DbContract.TimeEntry.COLUMN_NAME_DATE + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + DbContract.TimeEntry.COLUMN_NAME_REMINDER_ID + ") REFERENCES "
                + DbContract.ReminderEntry.TABLE_NAME + "(" + DbContract.ReminderEntry._ID +
                ") ON DELETE CASCADE)";

        db.execSQL(SQL_CREATE_REMINDER_TABLE);
        db.execSQL(SQL_CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + DbContract.ReminderEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS" + DbContract.LocationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS" + DbContract.TimeEntry.TABLE_NAME);
        onCreate(db);
    }
}
