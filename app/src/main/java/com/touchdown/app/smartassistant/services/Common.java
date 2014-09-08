package com.touchdown.app.smartassistant.services;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.DisplayMetrics;

import com.google.android.gms.maps.model.LatLng;
import com.touchdown.app.smartassistant.data.DbHelper;
import com.touchdown.app.smartassistant.models.ActionType;
import com.touchdown.app.smartassistant.models.Alarm;
import com.touchdown.app.smartassistant.models.RingerVolume;
import com.touchdown.app.smartassistant.models.Task;
import com.touchdown.app.smartassistant.models.TriggerLocation;

/**
 * Created by Pete on 4.8.2014.
 */
public class Common {
    public static final int TEST_REMINDER_DEFAULT_COUNT = 7;
    public static final String TASK_TAG = "task";

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
            Task task = new Task(-1, i + ". task", location);
            task.addAction(Alarm.createDefault());
            task.addAction(RingerVolume.createDefault());

            taskManager.insert(task);

         //   Reminder reminder = new Reminder(-1, i + ". reminder", new ReminderLocation(-1, -1, new LatLng(lat, 25), 100));

          //  reminderManager.insert(reminder);
        }
    }


    public static Alarm getDefaultTestReminder(){
        return new Alarm(-1, ActionType.ALARM, "test reminder content", true, -1);
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

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }
}
