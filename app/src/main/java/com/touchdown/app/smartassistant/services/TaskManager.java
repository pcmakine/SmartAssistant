package com.touchdown.app.smartassistant.services;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.touchdown.app.smartassistant.data.DatabaseManager;
import com.touchdown.app.smartassistant.models.Task;
import com.touchdown.app.smartassistant.views.OnGoingNotification;

import java.util.List;
import java.util.Observable;

/**
 * Created by Pete on 19.8.2014.
 */
public class TaskManager extends Observable{
    private static TaskManager sInstance;
    private DatabaseManager dbManager;

    private TaskManager(Context context){
        this.dbManager = DatabaseManager.getInstance(context);
    }

    public synchronized static TaskManager getInstance(Context context){
        if(sInstance == null){
            sInstance = new TaskManager(context);
        }
        return sInstance;
    }

    public long insert(Task task){
        long id = dbManager.insertTask(task);

        saveProximityAlert(task);

        OnGoingNotification.updateNotification();

        notifyDataObservers();

        updateLocationListeners();
        return id;
    }

    private void saveProximityAlert(Task task){
        if(task.isActive() && task.getLocation() != null && !task.getLocation().isPending()){
            ProximityAlarmManager.saveAlert(task);
        }
    }

    public boolean update(Task task){
        boolean success = dbManager.updateTask(task);

        notifyDataObservers();

        updateProximityAlarm(task);

        OnGoingNotification.updateNotification();

        updateLocationListeners();

        return success;
    }

    private void updateProximityAlarm(Task task){
        if(task.isActive() && task.getLocation() != null && !task.getLocation().isPending()){
            ProximityAlarmManager.updateAlert(task);
        }else{
            ProximityAlarmManager.removeAlert(task.getId());
        }
    }

    public Task findTaskById(long id){
        return dbManager.findTaskById(id);
    }

    public Cursor getAllTaskData(){
        return dbManager.getAllTaskData();
    }

    public List<Task> getAllTasks(){
        return dbManager.getAllTasks();
    }

    public List<Task> getAllTasksWithLocationTrigger(){
        return dbManager.getAllTasksWithLocationTrigger();
    }

    public int removeTask(long id){
        int rowsAffected = dbManager.removeTask(id);

        notifyDataObservers();

        ProximityAlarmManager.removeAlert(id);

        OnGoingNotification.updateNotification();

        updateLocationListeners();
        return rowsAffected;
    }

    public int getActiveTaskCount(){
        return dbManager.getActiveTaskCount();
    }

    private void notifyDataObservers(){
        setChanged();
        notifyObservers();
        clearChanged();
    }

    private void updateLocationListeners() {
        ApplicationContextProvider.getAppContext().startService(
                new Intent(ApplicationContextProvider.getAppContext(), LocationListenerManager.class));
    }

}
