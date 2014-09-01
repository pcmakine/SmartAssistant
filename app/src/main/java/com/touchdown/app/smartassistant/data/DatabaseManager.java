package com.touchdown.app.smartassistant.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import com.touchdown.app.smartassistant.models.Action;
import com.touchdown.app.smartassistant.models.Task;

import java.util.List;

/**
 * Created by Pete on 31.8.2014.
 */
public class DatabaseManager {
    private SQLiteOpenHelper dbHelper;
    private TaskDao taskDao;
    private AlarmDao reminderDao;
    private WriterDao wDao;
    private static DatabaseManager sInstance;

    private DatabaseManager(Context context){
        this.dbHelper = DbHelper.getInstance(context);
        this.taskDao = new TaskDao(dbHelper);
        this.reminderDao = new AlarmDao(dbHelper);
        this.wDao = new WriterDao(dbHelper);
    }

    public synchronized static DatabaseManager getInstance(Context context){
        if(sInstance == null){
            sInstance = new DatabaseManager(context);
        }
        return sInstance;
    }

    public long insertTask(Task task){
        long id = wDao.insert(task);
        insertActionsAndTriggers(task, id);
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

    public boolean updateTask(Task task){
        int rowsAffected = wDao.update(task);

        wDao.update(task.getTrigger());

        updateActions(task);

        return rowsAffected > 0;
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

    public List<Task> getAllTasksWithLocationTrigger(){
        return taskDao.getAllTasksWithLocation();
    }


    public int removeTask(long id){
        int rowsAffected = wDao.remove(id, DbContract.TaskEntry.TABLE_NAME, DbContract.TaskEntry._ID);
        return rowsAffected;
    }

    public int getActiveTaskCount(){
        return reminderDao.getActiveRemindersCount(DbContract.AlarmEntry.TABLE_NAME, DbContract.AlarmEntry.COLUMN_NAME_ON, 1);
    }


}
