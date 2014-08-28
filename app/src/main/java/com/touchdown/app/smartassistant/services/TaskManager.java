package com.touchdown.app.smartassistant.services;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import com.touchdown.app.smartassistant.data.ActionReminderDao;
import com.touchdown.app.smartassistant.data.DbContract;
import com.touchdown.app.smartassistant.data.DbHelper;
import com.touchdown.app.smartassistant.data.LocDao;
import com.touchdown.app.smartassistant.data.TaskDao;
import com.touchdown.app.smartassistant.data.WriterDao;
import com.touchdown.app.smartassistant.models.Action;
import com.touchdown.app.smartassistant.models.Task;
import com.touchdown.app.smartassistant.views.OnGoingNotification;

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

    private TaskManager(Context context){
        this.dbHelper = DbHelper.getInstance(context);
        this.taskDao = new TaskDao(dbHelper);
        this.reminderDao = new ActionReminderDao(dbHelper);
        this.locationDao = new LocDao(dbHelper);
        this.wDao = new WriterDao(dbHelper);
    }

    public synchronized static TaskManager getInstance(Context context){
        if(sInstance == null){
            sInstance = new TaskManager(context);
        }
        return sInstance;
    }

    public long insert(Task task){
        long id = wDao.insert(task);

        insertActionsAndTriggers(task, id);

        saveProximityAlert(task);

        OnGoingNotification.updateNotification();

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

    private void saveProximityAlert(Task task){
        if(task.isActive() && task.getLocation() != null && !task.getLocation().isPending()){
            ProximityAlarmManager.saveAlert(task);
        }
    }

    public boolean update(Task task){
        int rowsAffected = wDao.update(task);

        wDao.update(task.getTrigger());

        updateActions(task);

        notifyDataObservers();

        updateProximityAlarm(task);

        OnGoingNotification.updateNotification();

        stopTaskActivatorIfNoPendingTasksLeft();

        return rowsAffected > 0;
    }

    public void updateActions(Task task){
        List<Action> actions = task.getActions();

        for(Action action: actions){
            wDao.update(action);
        }
    }

    private void updateProximityAlarm(Task task){
        if(task.isActive() && task.getLocation() != null && !task.getLocation().isPending() ){
            ProximityAlarmManager.updateAlert(task);
        }else{
            ProximityAlarmManager.removeAlert(task.getId());
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

    public List<Task> getAllTasksWithLocationTrigger(){
        return taskDao.getAllTasksWithLocation();
    }

    public int removeTask(long id){
        int rowsAffected = wDao.remove(id, DbContract.TaskEntry.TABLE_NAME, DbContract.TaskEntry._ID);

        notifyDataObservers();

        ProximityAlarmManager.removeAlert(id);

        OnGoingNotification.updateNotification();

        stopTaskActivatorIfNoPendingTasksLeft();
        return rowsAffected;
    }

    public int getActiveTaskCount(){
        return reminderDao.getActiveRemindersCount(DbContract.ReminderEntry.TABLE_NAME, DbContract.ReminderEntry.COLUMN_NAME_ON, 1);
    }

    private void notifyDataObservers(){
        setChanged();
        notifyObservers();
        clearChanged();
    }

    private void stopTaskActivatorIfNoPendingTasksLeft() {
        ApplicationContextProvider.getAppContext().startService(
                new Intent(ApplicationContextProvider.getAppContext(), TaskActivatorKiller.class));
    }

}
