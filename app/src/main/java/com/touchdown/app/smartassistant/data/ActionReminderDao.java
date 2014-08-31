package com.touchdown.app.smartassistant.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.touchdown.app.smartassistant.models.Alarm;
import com.touchdown.app.smartassistant.services.Util;

/**
 * Created by Pete on 19.8.2014.
 */
public class ActionReminderDao extends Dao<Alarm> {

    public ActionReminderDao(SQLiteOpenHelper dbHelper) {
        super(dbHelper);
    }

    @Override
    protected Alarm buildObject(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(DbContract.ReminderEntry._ID));
        return buildObject(cursor, id);
    }

    public Alarm buildObject(Cursor cursor, long id) {
        long actionCollectionId = cursor.getLong(cursor.getColumnIndex(DbContract.ReminderEntry.COLUMN_NAME_TASK_ID));
        String content = cursor.getString(cursor.getColumnIndex(DbContract.ReminderEntry.COLUMN_NAME_CONTENT));
        int type = cursor.getInt(cursor.getColumnIndex(DbContract.ReminderEntry.COLUMN_NAME_TYPE));
        boolean isOn = Util.intAsBoolean(cursor.getInt(cursor.getColumnIndex(DbContract.ReminderEntry.COLUMN_NAME_ON)));

        boolean notificationEnabled = Util.intAsBoolean(cursor.getInt(cursor.getColumnIndex(DbContract.ReminderEntry.COLUMN_NAME_NOTIFICATION_ENABLED)));
        boolean fullscreenEnabled = Util.intAsBoolean(cursor.getInt(cursor.getColumnIndex(DbContract.ReminderEntry.COLUMN_NAME_FULLSCREEN_ENABLED)));

        Alarm alarm = new Alarm(id, type, content, isOn, actionCollectionId);

        alarm.enableNotification(notificationEnabled);
        alarm.enableFullScreen(fullscreenEnabled);

        return alarm;
    }

    public int getActiveRemindersCount(String tableName, String columnName, int value){
        return getDataByFieldInt(tableName, columnName, value).getCount();
    }

    public Alarm findReminderByTaskId(long taskId){        //for now allows only one reminder per task
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbContract.ReminderEntry.TABLE_NAME, null,
                DbContract.ReminderEntry.COLUMN_NAME_TASK_ID + " = ?",
                new String[] {String.valueOf(taskId)}, null, null, null, null);

        if(cursor != null && cursor.moveToFirst()){
        }else{
            return null;
        }
        return buildObject(cursor);
    }
}
