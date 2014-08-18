package com.touchdown.app.smartassistant.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.touchdown.app.smartassistant.Util;
import com.touchdown.app.smartassistant.models.ReminderLocation;
import com.touchdown.app.smartassistant.models.Reminder;

import java.util.List;

/**
 * Created by Pete on 18.8.2014.
 */
public class ReminderDao extends Dao<Reminder> {

    public ReminderDao(SQLiteOpenHelper dbHelper, String tableName, String idColumn){
        super(dbHelper, tableName, idColumn);
    }

    @Override
    protected ContentValues values(Reminder reminder) {
        ContentValues vals = new ContentValues();
        vals.put(DbContract.ReminderEntry.COLUMN_NAME_CONTENT, reminder.getContent());
        int onInteger = Util.booleanAsInt(reminder.isOn());
        vals.put(DbContract.ReminderEntry.COLUMN_NAME_ON, onInteger);
        return vals;
    }

    public int getActiveReminderCount(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor mCount= db.rawQuery("select count(*) from " + tableName +
                " where " + DbContract.ReminderEntry.COLUMN_NAME_ON + " = ?", new String[] {"1"});
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();
        return count;
    }

    @Override
    protected Reminder buildObject(Cursor cursor) {
        Long id = cursor.getLong(cursor.getColumnIndex(DbContract.ReminderEntry._ID));
        String content = cursor.getString(cursor.getColumnIndex(DbContract.ReminderEntry.COLUMN_NAME_CONTENT));
        boolean reminderOn = Util.intAsBoolean((cursor.getInt(cursor.getColumnIndex(DbContract.ReminderEntry.COLUMN_NAME_ON))));

        ReminderLocationDao locationDao = new ReminderLocationDao(dbHelper, DbContract.LocationEntry.TABLE_NAME, DbContract.LocationEntry._ID);
        ReminderLocation location = locationDao.getReminderLocation(id);

        Reminder reminder = new Reminder(id, content, location);

        reminder.setOn(reminderOn);

        return reminder;
    }

    @Override
    public List<Reminder> getList() {
        return null;
    }
}
