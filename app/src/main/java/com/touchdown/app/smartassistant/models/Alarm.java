package com.touchdown.app.smartassistant.models;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import com.touchdown.app.smartassistant.services.ApplicationContextProvider;
import com.touchdown.app.smartassistant.services.Util;
import com.touchdown.app.smartassistant.data.DbContract;
import com.touchdown.app.smartassistant.views.AlarmNotification;
import com.touchdown.app.smartassistant.views.FullscreenAlarmActivity;

import java.io.Serializable;

/**
 * Created by Pete on 19.8.2014.
 */
public class Alarm extends Action implements Serializable {
    private static final String TABLE_NAME = DbContract.ReminderEntry.TABLE_NAME;
    private static final String ID_COLUMN = DbContract.ReminderEntry._ID;

    private String content;
    private boolean notification;
    private boolean fullScreen;

    public Alarm(long id, int type, String content, boolean isOn, long actionCollectionId) {
        super(id, type, isOn, actionCollectionId);
        this.content = content;
        setTableName(TABLE_NAME);
        setIdColumn(ID_COLUMN);
    }

    public static Alarm createDefaultAlarm(){
        Alarm alarm = new Alarm(-1, 0, "", true, -1);
        alarm.enableNotification(true);
        alarm.enableFullScreen(false);
        return alarm;
    }

    public String getContent(){
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public void execute(){
        if(notification){
            new AlarmNotification(ApplicationContextProvider.getAppContext(), this).buildNotification();
        }
        if(fullScreen){
            Context context = ApplicationContextProvider.getAppContext();
            Intent i = new Intent(context, FullscreenAlarmActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

    public void enableNotification(boolean notificationEnabled){
        this.notification = notificationEnabled;
    }

    public void enableFullScreen(boolean fullScreenEnabled){
        this.fullScreen = fullScreenEnabled;
    }

    public boolean isNotificationEnabled(){
        return notification;
    }

    public boolean isFullScreenEnabled(){
        return fullScreen;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues vals = new ContentValues();
        vals.put(DbContract.ReminderEntry.COLUMN_NAME_CONTENT, content);
        int onInteger = Util.booleanAsInt(isOn());
        vals.put(DbContract.ReminderEntry.COLUMN_NAME_ON, onInteger);
        vals.put(DbContract.ReminderEntry.COLUMN_NAME_TASK_ID, this.getActionCollectionId());
        vals.put(DbContract.ReminderEntry.COLUMN_NAME_TYPE, this.getType());

        int fullScreenInt = Util.booleanAsInt(fullScreen);
        int notificationInt = Util.booleanAsInt(notification);

        vals.put(DbContract.ReminderEntry.COLUMN_NAME_NOTIFICATION_ENABLED, notificationInt);
        vals.put(DbContract.ReminderEntry.COLUMN_NAME_FULLSCREEN_ENABLED, fullScreenInt);

        return vals;
    }
}
