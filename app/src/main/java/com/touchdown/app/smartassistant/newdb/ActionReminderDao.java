package com.touchdown.app.smartassistant.newdb;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.touchdown.app.smartassistant.Util;
import com.touchdown.app.smartassistant.data.DbContract;

/**
 * Created by Pete on 19.8.2014.
 */
public class ActionReminderDao extends newDao<ActionReminder> {

    public ActionReminderDao(SQLiteOpenHelper dbHelper) {
        super(dbHelper);
    }

    @Override
    protected ActionReminder buildObject(Cursor cursor) {

        long id = cursor.getLong(cursor.getColumnIndex(DbContract.ReminderEntry._ID));
        long actionCollectionId = cursor.getLong(cursor.getColumnIndex(DbContract.ReminderEntry.COLUMN_NAME_TASK_ID));
        String content = cursor.getString(cursor.getColumnIndex(DbContract.ReminderEntry.COLUMN_NAME_CONTENT));
        int type = cursor.getInt(cursor.getColumnIndex(DbContract.ReminderEntry.COLUMN_NAME_TYPE));
        boolean isOn = Util.intAsBoolean(cursor.getInt(cursor.getColumnIndex(DbContract.ReminderEntry.COLUMN_NAME_ON)));

        ActionReminder reminder = new ActionReminder(id, type, content, isOn, actionCollectionId);

        return reminder;
    }

    public int getActiveRemindersCount(String tableName, String columnName, int value){
        return getDataByFieldInt(tableName, columnName, value).getCount();
    }

    public ActionReminder findReminderByTaskId(long taskId){        //for now allows only one reminder per task
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
