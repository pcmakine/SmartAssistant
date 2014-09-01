package com.touchdown.app.smartassistant.data;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.touchdown.app.smartassistant.data.DbContract.TaskEntry;
import com.touchdown.app.smartassistant.models.Alarm;
import com.touchdown.app.smartassistant.models.RingerVolume;
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

        LocDao locDao = new LocDao(dbHelper);
        TriggerLocation location = locDao.findByTaskId(id);

        Task task = new Task(id, name, location);
        addActions(task);

        return task;
    }

    private void addActions(Task task){
        addAlarm(task);
        addRingerVolume(task);
    }

    private void addRingerVolume(Task task){
        RingerVolumeDao ringerDao = new RingerVolumeDao(dbHelper);
        List<RingerVolume> ringerVolumes = ringerDao.findByFieldLong(DbContract.RingerVolumeEntry.TABLE_NAME,
                DbContract.RingerVolumeEntry.COLUMN_NAME_TASK_ID, task.getId());

        if(!ringerVolumes.isEmpty()){
            RingerVolume rVolume = ringerVolumes.get(0);            //should never have more than one entry
            task.addAction(rVolume);
        }
    }

    private void addAlarm(Task task){
        //todo could the object creation take place somewhere else so that these objects wouldn't have to be created every time we get task objects?
        AlarmDao reminderDao = new AlarmDao(dbHelper);
        Alarm alarm = reminderDao.findReminderByTaskId(task.getId());
        task.addAction(alarm);
    }

    public List<Task> getAllTasksWithLocation() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TaskEntry.TABLE_NAME + " JOIN " +
                DbContract.LocationEntry.TABLE_NAME + " ON " +
                TaskEntry.TABLE_NAME + "." + TaskEntry._ID + " = " +
                DbContract.LocationEntry.TABLE_NAME + "." + DbContract.LocationEntry._ID;

        Cursor cursor = db.rawQuery(query, null);
        List<Task> list = new ArrayList<Task>();

        while (cursor.moveToNext()) {
            list.add(buildLocationTask(cursor));
        }
            return list;
    }

 /*       String query = "SELECT * FROM " + TaskEntry.TABLE_NAME + " JOIN " +
                DbContract.LocationEntry.TABLE_NAME + " ON " +
                TaskEntry.TABLE_NAME + "." + TaskEntry._ID + " = " +
                DbContract.LocationEntry.TABLE_NAME + "." + DbContract.LocationEntry._ID +

                " JOIN " + DbContract.AlarmEntry.TABLE_NAME + " ON " +
                DbContract.LocationEntry.TABLE_NAME + "." + DbContract.LocationEntry.COLUMN_NAME_TASK_ID + " = " +
                DbContract.AlarmEntry.TABLE_NAME + "." + DbContract.AlarmEntry.COLUMN_NAME_TASK_ID +

                " JOIN " + DbContract.RingerVolumeEntry.TABLE_NAME + " ON " +
                DbContract.LocationEntry.TABLE_NAME + "." + DbContract.LocationEntry.COLUMN_NAME_TASK_ID + " = " +
                DbContract.RingerVolumeEntry.TABLE_NAME + "." + DbContract.RingerVolumeEntry.COLUMN_NAME_TASK_ID;

        Cursor cursor = db.rawQuery(query, null);

        List<Task> list = new ArrayList<Task>();

        while(cursor.moveToNext()){
            list.add(buildLocationTask(cursor));
        }*/



    private Task buildLocationTask(Cursor cursor){

        long taskId = cursor.getLong(cursor.getColumnIndex(TaskEntry._ID));
        int locationIdIndex = cursor.getColumnIndex(TaskEntry.COLUMN_NAME_TASK_NAME) + 1;

        long locationId = cursor.getLong(locationIdIndex);
        String name = cursor.getString(cursor.getColumnIndex(TaskEntry.COLUMN_NAME_TASK_NAME));

        TriggerLocation location = new LocDao(dbHelper).buildObject(cursor, locationId);

        Task task = new Task(taskId, name, location);

        addActions(task);

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