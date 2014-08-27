package com.touchdown.app.smartassistant.data;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.touchdown.app.smartassistant.data.DbContract.TaskEntry;
import com.touchdown.app.smartassistant.models.Action;
import com.touchdown.app.smartassistant.views.NotificationReminder;
import com.touchdown.app.smartassistant.models.Task;
import com.touchdown.app.smartassistant.models.TriggerLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pete on 19.8.2014.
 */
public class TaskDao extends Dao<Task> {

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

    public List<Task> getAllTasksWithLocation(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + TaskEntry.TABLE_NAME + " JOIN " +
                DbContract.LocationEntry.TABLE_NAME + " ON " +
                TaskEntry.TABLE_NAME + "." + TaskEntry._ID + " = " +
                DbContract.LocationEntry.TABLE_NAME + "." + DbContract.LocationEntry._ID +
                " JOIN " + DbContract.ReminderEntry.TABLE_NAME + " ON " +
                DbContract.LocationEntry.TABLE_NAME + "." + DbContract.LocationEntry.COLUMN_NAME_TASK_ID + " = " +
                DbContract.ReminderEntry.TABLE_NAME + "." + DbContract.ReminderEntry.COLUMN_NAME_TASK_ID;

        Cursor cursor = db.rawQuery(query, null);

        List<Task> list = new ArrayList<Task>();

        while(cursor.moveToNext()){
            list.add(buildLocationTask(cursor));
        }

        return list;
    }

    private Task buildLocationTask(Cursor cursor){
        long taskId = cursor.getLong(cursor.getColumnIndex(TaskEntry._ID));
        int locationIdIndex = cursor.getColumnIndex(TaskEntry.COLUMN_NAME_TASK_NAME) + 1;

        long locationId = cursor.getLong(locationIdIndex);

        int reminderIndex = cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_TASK_ID) + 1;
        long reminderId = cursor.getLong(reminderIndex);

        String name = cursor.getString(cursor.getColumnIndex(TaskEntry.COLUMN_NAME_TASK_NAME));

        TriggerLocation location = new LocDao(dbHelper).buildObject(cursor, locationId);
        NotificationReminder reminder = new ActionReminderDao(dbHelper).buildObject(cursor, reminderId);

        Task task = new Task(taskId, name, location, reminder);

        return task;

    }
}



/*      String locationIdAlias = "location";
        String reminderIdAlias = "reminder";

        String query = "SELECT " + TaskEntry.TABLE_NAME + "." + TaskEntry._ID  + ", " +
                TaskEntry.COLUMN_NAME_TASK_NAME + ", " +
                DbContract.LocationEntry.TABLE_NAME + "." + DbContract.LocationEntry._ID + " AS " + locationIdAlias + ", " +
                DbContract.LocationEntry.COLUMN_NAME_TASK_ID + ", " +
                DbContract.LocationEntry.COLUMN_NAME_RADIUS + ", " +
                DbContract.LocationEntry.COLUMN_NAME_LAT + ", " +
                DbContract.LocationEntry.COLUMN_NAME_LONG + ", " +
                DbContract.LocationEntry.COLUMN_NAME_TRIGGER_ON_ARRIVAL + ", " +
                DbContract.LocationEntry.COLUMN_NAME_TRIGGER_ON_DEPARTURE + ", " +
                DbContract.LocationEntry.COLUMN_NAME_TRIGGER_TYPE + ", " +
                DbContract.ReminderEntry.TABLE_NAME + "." + DbContract.ReminderEntry._ID + " AS " + reminderIdAlias + ", " +
                DbContract.ReminderEntry.COLUMN_NAME_TASK_ID + ", " +
                DbContract.ReminderEntry.COLUMN_NAME_ON + ", " +
                DbContract.ReminderEntry.COLUMN_NAME_TYPE + ", " +
                DbContract.ReminderEntry.COLUMN_NAME_CONTENT +

                " FROM " + TaskEntry.TABLE_NAME + " JOIN " +
                DbContract.LocationEntry.TABLE_NAME + " ON " + TaskEntry.TABLE_NAME + "."
                + TaskEntry._ID + " = " + DbContract.LocationEntry.TABLE_NAME + "." + DbContract.LocationEntry.COLUMN_NAME_TASK_ID +
                " JOIN " + DbContract.ReminderEntry.TABLE_NAME + " ON " + DbContract.LocationEntry.TABLE_NAME + "."
                + DbContract.LocationEntry.COLUMN_NAME_TASK_ID + " = " + DbContract.ReminderEntry.TABLE_NAME + "." + DbContract.ReminderEntry.COLUMN_NAME_TASK_ID;*/