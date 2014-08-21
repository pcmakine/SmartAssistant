package com.touchdown.app.smartassistant.services;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import com.touchdown.app.smartassistant.data.DbContract;
import com.touchdown.app.smartassistant.data.DbHelper;
import com.touchdown.app.smartassistant.data.ReminderDao;
import com.touchdown.app.smartassistant.data.ReminderLocationDao;
import com.touchdown.app.smartassistant.models.ReminderLocation;
import com.touchdown.app.smartassistant.models.Reminder;
import com.touchdown.app.smartassistant.views.OnGoingNotification;
import java.util.List;
import java.util.Observable;

/**
 * Created by Pete on 3.8.2014.
 */

/**
 * Class for persisting the reminders to database and doing related actions. Implemented as a singleton.
 */
public class ReminderManager extends Observable{
    private static ReminderManager sInstance;
    private ReminderDao reminderDao;
    private ReminderLocationDao locationDao;

    private ReminderManager(SQLiteOpenHelper dbHelper){
        this.reminderDao = new ReminderDao(dbHelper, DbContract.ReminderEntry.TABLE_NAME,
                DbContract.ReminderEntry._ID);
        this.locationDao = new ReminderLocationDao(dbHelper,
                DbContract.LocationEntry.TABLE_NAME, DbContract.LocationEntry._ID);
    }

    public synchronized static ReminderManager getInstance(Context context){
        if(sInstance == null){
            sInstance = new ReminderManager(new DbHelper(context.getApplicationContext()));
        }
        return sInstance;
    }

    public long insert(Reminder reminder){
        //long id = reminderDao.insert(reminder); //insertReminderInDatabase(reminder);
/*        reminder.setId(id);
        insertRemindersLocationInDatabase(reminder);
        notifyDataObservers();

        if(reminder.isOn() && reminder.getReminderLocation() != null){
            ProximityAlarmManager.saveAlert(reminder);
        }

        OnGoingNotification.updateNotification();*/

        return -1;
    }

    private void insertRemindersLocationInDatabase(Reminder reminder){
/*        ReminderLocation reminderLocation = reminder.getReminderLocation();
        if(reminderLocation != null){
            reminderLocation.setReminderId(reminder.getId());
            locationDao.insert(reminderLocation);
        }*/
    }

    public Cursor getAllReminderData(){
        return null;
        // return reminderDao.getAll();
    }

    public boolean update(Reminder reminder) {
/*        int numOfRowsAffected = reminderDao.update(reminder, reminder.getId());

        if(reminder.getReminderLocation() != null){
            locationDao.update(reminder.getReminderLocation(),
                    reminder.getReminderLocation().getId());
        }
        notifyDataObservers();

        updateProximityAlarm(reminder);

        OnGoingNotification.updateNotification();*/

        return true; //numOfRowsAffected > 0;
    }

    private void updateProximityAlarm(Reminder reminder){
        if(reminder.isOn() && reminder.getReminderLocation() != null){
            ProximityAlarmManager.updateAlert(reminder);
        }else{
            ProximityAlarmManager.removeAlert(reminder.getId());
        }
    }

    public Reminder getOne(long id){
        return null;
       // return reminderDao.getOne(id);
    }

    public int remove(long id){
/*        int rowsAffected = reminderDao.remove(id);
        notifyDataObservers();

        ProximityAlarmManager.removeAlert(id);

        OnGoingNotification.updateNotification();*/

        return 0; //rowsAffected;
    }

    public List<Reminder> getReminderList(){
        return null;
       // return reminderDao.getAllAsList();
    }

    public int getActiveReminderCount(){
        return reminderDao.getActiveReminderCount();
    }

    private void notifyDataObservers(){
        setChanged();
        notifyObservers();
        clearChanged();
    }
}
