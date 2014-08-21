package com.touchdown.app.smartassistant.newdb;

import android.content.ContentValues;

import com.touchdown.app.smartassistant.Util;
import com.touchdown.app.smartassistant.data.DbContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pete on 19.8.2014.
 */
public class ActionReminder extends Action {
    private static final String TABLE_NAME = DbContract.ReminderEntry.TABLE_NAME;
    private static final String ID_COLUMN = DbContract.ReminderEntry._ID;

    private String content;

    public ActionReminder(long id, int type, String content, boolean isOn, long actionCollectionId) {
        super(id, type, isOn, actionCollectionId);
        this.content = content;
        setTableName(TABLE_NAME);
        setIdColumn(ID_COLUMN);
    }

    public String getContent(){
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues vals = new ContentValues();
        vals.put(DbContract.ReminderEntry.COLUMN_NAME_CONTENT, content);
        int onInteger = Util.booleanAsInt(isOn());
        vals.put(DbContract.ReminderEntry.COLUMN_NAME_ON, onInteger);
        vals.put(DbContract.ReminderEntry.COLUMN_NAME_TASK_ID, this.getActionCollectionId());
        vals.put(DbContract.ReminderEntry.COLUMN_NAME_TYPE, this.getType());
        return vals;
    }
}
