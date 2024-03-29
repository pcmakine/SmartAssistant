package com.touchdown.app.smartassistant.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;
import com.touchdown.app.smartassistant.services.Common;
import com.touchdown.app.smartassistant.models.TriggerLocation;

/**
 * Created by Pete on 18.8.2014.
 */
public class LocDao extends Dao<TriggerLocation> {

    public LocDao(SQLiteOpenHelper dbHelper) {
        super(dbHelper);
    }


    @Override
    protected TriggerLocation buildObject(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(DbContract.LocationEntry._ID));
        return buildObject(cursor, id);
    }

    public TriggerLocation buildObject(Cursor cursor, long id){
        double lat = cursor.getDouble(cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_LAT));
        double longitude = cursor.getDouble(cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_LONG));

        int radius = cursor.getInt(cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_RADIUS));

        long parentId = cursor.getLong(cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_TASK_ID));

        TriggerLocation loc = new TriggerLocation(id, new LatLng(lat, longitude), radius, parentId);

        boolean arrivalTriggerOn = Common.intAsBoolean(cursor.getInt(cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_TRIGGER_ON_ARRIVAL)));
        boolean departureTriggerOn = Common.intAsBoolean(cursor.getInt(cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_TRIGGER_ON_DEPARTURE)));
        boolean pending = Common.intAsBoolean(cursor.getInt(cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_PENDING)));

        loc.setArrivalTrigger(arrivalTriggerOn);
        loc.setDepartureTrigger(departureTriggerOn);
        loc.setPending(pending);

        return loc;
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

        TriggerLocation location = buildObject(cursor);
        return location;
    }

}
