package com.touchdown.app.smartassistant.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.touchdown.app.smartassistant.models.ActionType;
import com.touchdown.app.smartassistant.models.Alarm;
import com.touchdown.app.smartassistant.services.Util;

/**
 * Created by Pete on 19.8.2014.
 */
public class AlarmDao extends Dao<Alarm> {

    public AlarmDao(SQLiteOpenHelper dbHelper) {
        super(dbHelper);
    }

    @Override
    protected Alarm buildObject(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(DbContract.AlarmEntry._ID));
        return buildObject(cursor, id);
    }

    public Alarm buildObject(Cursor cursor, long id) {
        long actionCollectionId = cursor.getLong(cursor.getColumnIndex(DbContract.AlarmEntry.COLUMN_NAME_TASK_ID));
        String content = cursor.getString(cursor.getColumnIndex(DbContract.AlarmEntry.COLUMN_NAME_CONTENT));
        int type = cursor.getInt(cursor.getColumnIndex(DbContract.AlarmEntry.COLUMN_NAME_TYPE));
        boolean isOn = Util.intAsBoolean(cursor.getInt(cursor.getColumnIndex(DbContract.AlarmEntry.COLUMN_NAME_ON)));

        boolean notificationEnabled = Util.intAsBoolean(cursor.getInt(cursor.getColumnIndex(DbContract.AlarmEntry.COLUMN_NAME_NOTIFICATION_ENABLED)));
        boolean fullscreenEnabled = Util.intAsBoolean(cursor.getInt(cursor.getColumnIndex(DbContract.AlarmEntry.COLUMN_NAME_FULLSCREEN_ENABLED)));

        Alarm alarm = new Alarm(id, ActionType.ALARM, content, isOn, actionCollectionId);

        alarm.enableNotification(notificationEnabled);
        alarm.enableFullScreen(fullscreenEnabled);

        return alarm;
    }

    public int getActiveRemindersCount(String tableName, String columnName, long value){
        return getDataByFieldLong(tableName, columnName, value).getCount();
    }

    public Alarm findReminderByTaskId(long taskId){        //for now allows only one reminder per task
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbContract.AlarmEntry.TABLE_NAME, null,
                DbContract.AlarmEntry.COLUMN_NAME_TASK_ID + " = ?",
                new String[] {String.valueOf(taskId)}, null, null, null, null);

        if(cursor != null && cursor.moveToFirst()){
        }else{
            return null;
        }
        return buildObject(cursor);
    }
}
