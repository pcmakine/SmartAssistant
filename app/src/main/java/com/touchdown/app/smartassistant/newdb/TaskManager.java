package com.touchdown.app.smartassistant.newdb;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import com.touchdown.app.smartassistant.data.DbContract;

import java.util.List;
import java.util.Observable;

/**
 * Created by Pete on 19.8.2014.
 */
public class TaskManager extends Observable{
    private static TaskManager sInstance;
    private TaskDao taskDao;
    private ActionReminderDao reminderDao;
    private LocDao locationDao;
    private WriterDao wDao;
    private SQLiteOpenHelper dbHelper;

    private TaskManager(SQLiteOpenHelper dbHelper){
        this.taskDao = new TaskDao(dbHelper);
        this.reminderDao = new ActionReminderDao(dbHelper);
        this.locationDao = new LocDao(dbHelper);
        this.wDao = new WriterDao(dbHelper);
    }

    public synchronized static TaskManager getInstance(SQLiteOpenHelper dbHelper){
        if(sInstance == null){
            sInstance = new TaskManager(dbHelper);
        }
        return sInstance;
    }

    public long insert(Task task){
        long id = wDao.insert(task);
        insertActionsAndTriggers(task, id);

/*        long id = reminderDao.insert(reminder); //insertReminderInDatabase(reminder);
        reminder.setId(id);
        insertRemindersLocationInDatabase(reminder);


        if(reminder.isOn() && reminder.getReminderLocation() != null){
            ProximityAlarmManager.saveAlert(reminder);
        }

        OnGoingNotification.updateNotification();*/

        notifyDataObservers();

        return id;
    }

    private void insertActionsAndTriggers(Task task, long id){
        task.setIdForThisAndChildObjects(id);

        wDao.insert(task.getTrigger()); //todo only supports one trigger for now

        insertActions(task);
    }

    private void insertActions(Task task){
        List<Action> actions = task.getActions();
        for(Action action: actions){
            wDao.insert(action);
        }
    }

    private void notifyDataObservers(){
        setChanged();
        notifyObservers();
        clearChanged();
    }

    public void update(Task task){
        wDao.update(task);

        wDao.update(task.getTrigger());

        updateActions(task);

    }

    public void updateActions(Task task){
        List<Action> actions = task.getActions();

        for(Action action: actions){
            wDao.update(action);
        }
    }

    public Task findTaskById(long id){
        Task task = taskDao.getOne(id, DbContract.TaskEntry.TABLE_NAME, DbContract.TaskEntry._ID);
        return task;
    }

    public Cursor getAllTaskData(){
        return taskDao.getAll(DbContract.TaskEntry.TABLE_NAME, DbContract.TaskEntry._ID);
    }

    public List<Task> getAllTasks(){
        return taskDao.getAllAsList(DbContract.TaskEntry.TABLE_NAME, DbContract.TaskEntry._ID);
    }

    public int remove(Task task){
        int rowsAffected = wDao.remove(task);
        return rowsAffected;
    }

    public int getActiveTaskCount(){
        return reminderDao.getActiveRemindersCount(DbContract.ReminderEntry.TABLE_NAME, DbContract.ReminderEntry.COLUMN_NAME_ON, 1);
    }

}
