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
    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "smartassist.db";

    public static DbHelper sInstance;

    public DbHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public synchronized  static SQLiteOpenHelper getInstance(Context context){
        if(sInstance == null){
            sInstance = new DbHelper(context.getApplicationContext());
        }
        return sInstance;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_TASK_TABLE = "CREATE TABLE " + TaskEntry.TABLE_NAME +
                " (" + TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TaskEntry.COLUMN_NAME_TASK_NAME + " TEXT NOT NULL)";

        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + LocationEntry.TABLE_NAME +
                " (" + LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                LocationEntry.COLUMN_NAME_TRIGGER_TYPE + " INTEGER NOT NULL, " +
                LocationEntry.COLUMN_NAME_LAT + " REAL NOT NULL, " +
                LocationEntry.COLUMN_NAME_LONG + " REAL NOT NULL, " +
                LocationEntry.COLUMN_NAME_RADIUS + " INTEGER NOT NULL, " +
                LocationEntry.COLUMN_NAME_TRIGGER_ON_ARRIVAL + " INTEGER NOT NULL, " +
                LocationEntry.COLUMN_NAME_TRIGGER_ON_DEPARTURE + " INTEGER NOT NULL, " +
                LocationEntry.COLUMN_NAME_PENDING + " INTEGER NOT NULL, " +
                LocationEntry.COLUMN_NAME_TASK_ID + " INTEGER NOT NULL, " +             //MUST BE CREATED AS THE LAST COLUMN FOR TASKDAO TO WORK
                " FOREIGN KEY (" + LocationEntry.COLUMN_NAME_TASK_ID + ") REFERENCES " +
                TaskEntry.TABLE_NAME + "(" + TaskEntry._ID + ") " +
                " ON DELETE CASCADE)";


        final String SQL_CREATE_REMINDER_TABLE = "CREATE TABLE " + AlarmEntry.TABLE_NAME
                + " (" + AlarmEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                AlarmEntry.COLUMN_NAME_CONTENT + " TEXT NOT NULL, " +
                AlarmEntry.COLUMN_NAME_ON + " INTEGER NOT NULL, " +
                AlarmEntry.COLUMN_NAME_TYPE + " INTEGER NOT NULL, " +
                AlarmEntry.COLUMN_NAME_NOTIFICATION_ENABLED + " INTEGER NOT NULL, " +
                AlarmEntry.COLUMN_NAME_FULLSCREEN_ENABLED + " INTEGER NOT NULL, " +
                AlarmEntry.COLUMN_NAME_TASK_ID + " INTEGER NOT NULL UNIQUE, " +
                " FOREIGN KEY (" + AlarmEntry.COLUMN_NAME_TASK_ID + ") REFERENCES " +
                TaskEntry.TABLE_NAME + "(" + TaskEntry._ID + ")" +
                " ON DELETE CASCADE)";

        final String SQL_CREATE_RINGER_VOLUME_TABLE = "CREATE TABLE " + RingerVolumeEntry.TABLE_NAME
                + " (" + RingerVolumeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                RingerVolumeEntry.COLUMN_NAME_VOLUME + " INTEGER NOT NULL, " +
                RingerVolumeEntry.COLUMN_NAME_ON + " INTEGER NOT NULL, " +
                RingerVolumeEntry.COLUMN_NAME_TYPE + " INTEGER NOT NULL, " +
                RingerVolumeEntry.COLUMN_NAME_TASK_ID + " INTEGER NOT NULL UNIQUE, " +
                " FOREIGN KEY (" + RingerVolumeEntry.COLUMN_NAME_TASK_ID + ") REFERENCES " +
                TaskEntry.TABLE_NAME + "(" + TaskEntry._ID + ")" +
                " ON DELETE CASCADE)";

        db.execSQL(SQL_CREATE_TASK_TABLE);
        db.execSQL(SQL_CREATE_LOCATION_TABLE);
        db.execSQL(SQL_CREATE_REMINDER_TABLE);
        db.execSQL(SQL_CREATE_RINGER_VOLUME_TABLE);

        Log.d(LOG_TAG, SQL_CREATE_TASK_TABLE);
        Log.d(LOG_TAG, SQL_CREATE_LOCATION_TABLE);
        Log.d(LOG_TAG, SQL_CREATE_REMINDER_TABLE);
        Log.d(LOG_TAG, SQL_CREATE_RINGER_VOLUME_TABLE);

/*        final String SQL_CREATE_ACTION_TRIGGER_TABLE = "CREATE TABLE " + ActionTriggerEntry.TABLE_NAME +
                " (" + ActionTriggerEntry.COLUMN_NAME_TASK_ID + " INTEGER NOT NULL, " +
                ActionTriggerEntry.COLUMN_NAME_TRIGGER_ID + " INTEGER NOT NULL, " +
                " PRIMARY KEY (" + ActionTriggerEntry.COLUMN_NAME_TASK_ID + ", " +
                ActionTriggerEntry.COLUMN_NAME_TRIGGER_ID + ")" +
                " FOREIGN KEY (" + ActionTriggerEntry.COLUMN_NAME_TASK_ID + ") REFERENCES "
                + ActionEntry.TABLE_NAME + "(" + ActionEntry._ID +")" +
                " FOREIGN KEY (" + ActionTriggerEntry.COLUMN_NAME_TRIGGER_ID + ") REFERENCES "
                + TriggerEntry.TABLE_NAME + "(" + TriggerEntry._ID + ") " +
                "ON DELETE CASCADE)";*/

 /*       final String SQL_CREATE_TRIGGER_TABLE = "CREATE TABLE " + TriggerEntry.TABLE_NAME +
                " (" + TriggerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                TriggerEntry.COLUMN_NAME_TASK_ID + " INTEGER NOT NULL, " +
                TriggerEntry.COLUMN_NAME_TRIGGER_TYPE + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + TriggerEntry.COLUMN_NAME_TASK_ID + ") REFERENCES " +
                TaskEntry.TABLE_NAME + "(" + TaskEntry._ID + "))";*/


/*        final String SQL_CREATE_TIME_TABLE = "CREATE TABLE " + DbContract.TimeEntry.TABLE_NAME +
                " (" + DbContract.TimeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                TimeEntry.COLUMN_NAME_REMINDER_ID + " INTEGER, " +
                TimeEntry.COLUMN_NAME_DATE + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + TimeEntry.COLUMN_NAME_REMINDER_ID + ") REFERENCES "
                + ReminderEntry.TABLE_NAME + "(" + ReminderEntry._ID +
                ") ON DELETE CASCADE)";*/

        // db.execSQL(SQL_CREATE_REMINDER_TABLE);
        // db.execSQL(SQL_CREATE_LOCATION_TABLE);

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AlarmEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.LocationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS  " + DbContract.TimeEntry.TABLE_NAME);
        onCreate(db);
    }
}
