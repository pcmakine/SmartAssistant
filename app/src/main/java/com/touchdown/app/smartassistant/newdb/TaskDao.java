package com.touchdown.app.smartassistant.newdb;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import com.touchdown.app.smartassistant.data.Dao;
import com.touchdown.app.smartassistant.data.DbContract;
import com.touchdown.app.smartassistant.data.DbContract.TaskEntry;

/**
 * Created by Pete on 19.8.2014.
 */
public class TaskDao extends newDao<Task> {

    public TaskDao(SQLiteOpenHelper dbHelper) {
        super(dbHelper);
    }

    @Override
    protected Task buildObject(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(TaskEntry._ID));
        String name = cursor.getString(cursor.getColumnIndex(TaskEntry.COLUMN_NAME_TASK_NAME));

        //todo could the object creation take place somewhere else so that these objects wouldn't have to be created every time we get task objects?
        ActionReminderDao reminderDao = new ActionReminderDao(dbHelper);
        Action action = reminderDao.findReminderByTaskId(id);

        LocDao locDao = new LocDao(dbHelper);
        TriggerLocation location = locDao.findByTaskId(id);

        return new Task(id, name, location, action);
    }
}
