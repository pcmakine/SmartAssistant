package com.touchdown.app.smartassistant.services;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;
import com.touchdown.app.smartassistant.data.DbHelper;
import com.touchdown.app.smartassistant.models.Alarm;
import com.touchdown.app.smartassistant.models.Task;
import com.touchdown.app.smartassistant.models.TriggerLocation;

/**
 * Created by Pete on 4.8.2014.
 */
public class Util {
    public static final int TEST_REMINDER_DEFAULT_COUNT = 7;

    private static final double TEST_LAT = 60;
    private static final double TEST_LONG = 25;

    public static void clearAndInsertTestData(Context context, SQLiteOpenHelper dbHelper){
        clearDb(context, dbHelper);
        insertTestData(context, TEST_REMINDER_DEFAULT_COUNT);
    }

    public static void clearDb(Context context, SQLiteOpenHelper dbHelper){

        context.deleteDatabase(DbHelper.DATABASE_NAME);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.close();
    //    db.delete(DbContract.TaskEntry.TABLE_NAME, null, null);
        //context.openOrCreateDatabase(DbHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
    }

    public static void insertTestData(Context context, int numberOfRecords){
        TaskManager taskManager = TaskManager.getInstance(context);

        for (int i = 0; i < numberOfRecords; i++){
            double lat = 60 + i*0.1;
            TriggerLocation location = new TriggerLocation(-1, new LatLng(lat, TEST_LONG), TriggerLocation.DEFAULT_RADIUS, -1);
            Task task = new Task(-1, i + ". task", location, getDefaultTestReminder());

            taskManager.insert(task);

         //   Reminder reminder = new Reminder(-1, i + ". reminder", new ReminderLocation(-1, -1, new LatLng(lat, 25), 100));

          //  reminderManager.insert(reminder);
        }
    }


    public static Alarm getDefaultTestReminder(){
        return new Alarm(-1, 0, "test reminder content", true, -1);
    }

    public static TriggerLocation getDefaultTestLocation(){
        return new TriggerLocation(-1, new LatLng(TEST_LAT, TEST_LONG), TriggerLocation.DEFAULT_RADIUS, -1);
    }

    public static int booleanAsInt(boolean truthVal){
        return (truthVal)? 1: 0;
    }

    public static boolean intAsBoolean(int value){
        return value == 1;
    }


    public static int msToSec(long ms){
        return (int) (ms/1000);
    }

    public static long secondsToMs(int secs){
        return secs*1000;
    }
}
