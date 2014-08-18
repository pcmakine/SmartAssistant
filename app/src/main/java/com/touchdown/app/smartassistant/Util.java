package com.touchdown.app.smartassistant;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;
import com.touchdown.app.smartassistant.data.DbHelper;
import com.touchdown.app.smartassistant.models.ReminderLocation;
import com.touchdown.app.smartassistant.models.Reminder;
import com.touchdown.app.smartassistant.services.ReminderManager;

/**
 * Created by Pete on 4.8.2014.
 */
public class Util {
    public static final int TEST_REMINDER_DEFAULT_COUNT = 10;

    public static void clearAndInsertTestData(SQLiteOpenHelper dbHelper, Context context){
        clearDb(dbHelper, context);
        insertTestData(context, TEST_REMINDER_DEFAULT_COUNT);
    }

    public static void clearDb(SQLiteOpenHelper dbHelper, Context context){
        context.deleteDatabase(DbHelper.DATABASE_NAME);
       // SQLiteDatabase db = dbHelper.getWritableDatabase();
    }

    public static void insertTestData(Context context, int numberOfRecords){
        ReminderManager reminderManager = ReminderManager.getInstance(context);
        for (int i = 0; i < numberOfRecords; i++){
            double lat = 60 + i*0.1;
            Reminder reminder = new Reminder(-1, i + ". reminder", new ReminderLocation(-1, -1, new LatLng(lat, 25), 100));

            reminderManager.insert(reminder);
        }
    }

    public static int booleanAsInt(boolean truthVal){
       return (truthVal)? 1: 0;
    }

    public static boolean intAsBoolean(int value){
        return value == 1;
    }
}
