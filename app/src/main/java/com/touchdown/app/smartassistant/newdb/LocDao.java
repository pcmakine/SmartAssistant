package com.touchdown.app.smartassistant.newdb;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;
import com.touchdown.app.smartassistant.data.Dao;
import com.touchdown.app.smartassistant.data.DbContract;

import java.util.List;

/**
 * Created by Pete on 18.8.2014.
 */
public class LocDao extends newDao<TriggerLocation> {

    public LocDao(SQLiteOpenHelper dbHelper) {
        super(dbHelper);
    }

/*    @Override
    public long insert(TriggerLocation location){
        TriggerDao triggerDao = new TriggerDao(dbHelper, DbContract.TriggerEntry.TABLE_NAME, DbContract.TriggerEntry._ID);
        Trigger trigger = new Trigger(-1, TriggerLocation.TRIGGER_TYPE, );
        long parentId = triggerDao.insert(trigger);
        location.setParentId(parentId);
        return super.insert(location);
    }*/


    @Override
    protected TriggerLocation buildObject(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(DbContract.LocationEntry._ID));
        double lat = cursor.getDouble(cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_LAT));
        double longitude = cursor.getDouble(cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_LONG));

        int radius = cursor.getInt(cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_RADIUS));

        long parentId = cursor.getLong(cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_TASK_ID));

        return new TriggerLocation(id, new LatLng(lat, longitude), radius, parentId);
    }

    public TriggerLocation findByTaskId(long taskId){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbContract.LocationEntry.TABLE_NAME, null,
                DbContract.LocationEntry.COLUMN_NAME_TASK_ID + " = ?",
                new String[] {String.valueOf(taskId)}, null, null, null, null);

        if(cursor != null && cursor.moveToFirst()){
        }else{
            return null;
        }
        return buildObject(cursor);
    }

}
