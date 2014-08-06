package com.touchdown.app.smartassistant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;
import com.touchdown.app.smartassistant.data.DbHelper;
import com.touchdown.app.smartassistant.models.LocationDao;
import com.touchdown.app.smartassistant.models.ReminderDao;

/**
 * Created by Pete on 4.8.2014.
 */
public class Util {
    public static final int TEST_REMINDER_COUNT = 2;

    public static void clearAndInsertTestData(SQLiteOpenHelper dbHelper, Context context){
        clearDb(dbHelper, context);
        insertTestData(dbHelper);
    }

    public static void clearDb(SQLiteOpenHelper dbHelper, Context context){
        context.deleteDatabase(DbHelper.DATABASE_NAME);
        SQLiteDatabase db = new DbHelper(context).getWritableDatabase();
    }

    public static void insertTestData(SQLiteOpenHelper dbHelper){
        for (int i = 0; i < TEST_REMINDER_COUNT; i++){
            double lat = 60 + i*0.1;
            ReminderDao reminder = new ReminderDao(-1, i + ". reminder", new LocationDao(-1, -1, new LatLng(lat, 25)));

            reminder.insert(dbHelper);
        }

    }
}