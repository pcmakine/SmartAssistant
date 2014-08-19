package com.touchdown.app.smartassistant.newdb;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import com.touchdown.app.smartassistant.data.Dao;
import com.touchdown.app.smartassistant.data.DbContract;

/**
 * Created by Pete on 19.8.2014.
 */
public class TriggerDao extends Dao<Trigger> {

    public TriggerDao(SQLiteOpenHelper dbHelper, String tableName, String idColumn) {
        super(dbHelper, tableName, idColumn);
    }

    @Override
    protected ContentValues values(Trigger trigger) {
        ContentValues vals = new ContentValues();
        vals.put(DbContract.TriggerEntry.COLUMN_NAME_TRIGGER_TYPE, trigger.getType());
        return vals;
    }

    @Override
    protected Trigger buildObject(Cursor cursor) {
/*        int type = cursor.getInt(cursor.getColumnIndex(DbContract.TriggerEntry.COLUMN_NAME_TRIGGER_TYPE));
        long id = cursor.getLong(cursor.getColumnIndex(DbContract.TriggerEntry._ID));
        if(type == TriggerLocation.TRIGGER_TYPE){
            LocDao locDao = new LocDao(dbHelper, DbContract.LocationEntry.TABLE_NAME, DbContract.LocationEntry._ID);
            return locDao.getOne(id);
        }*/

        int type = cursor.getInt(cursor.getColumnIndex(DbContract.TriggerEntry.COLUMN_NAME_TRIGGER_TYPE));
        long id = cursor.getLong(cursor.getColumnIndex(DbContract.TriggerEntry._ID));

        Trigger trigger = new Trigger(id, type);
        return trigger;
    }
}
