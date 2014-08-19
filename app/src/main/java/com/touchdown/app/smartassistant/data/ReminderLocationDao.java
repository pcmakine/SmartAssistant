package com.touchdown.app.smartassistant.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;
import com.touchdown.app.smartassistant.models.ReminderLocation;

import java.util.List;

/**
 * Created by Pete on 18.8.2014.
 */
public class ReminderLocationDao extends Dao<ReminderLocation> {


    public ReminderLocationDao(SQLiteOpenHelper dbHelper, String tableName, String idColumn) {
        super(dbHelper, tableName, idColumn);
    }

    public ReminderLocation getReminderLocation(long id){
      /*  SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbContract.LocationEntry.TABLE_NAME, null,
                DbContract.LocationEntry.COLUMN_NAME_REMINDER_ID + " = ?",
                new String[] {String.valueOf(id)}, null, null, null, null);

        if(cursor != null && cursor.moveToFirst()){
        }else{
            return null;
        }
        return buildObject(cursor);*/
        return null;
    }

    @Override
    protected ContentValues values(ReminderLocation reminderLocation) {
        ContentValues vals = new ContentValues();
        vals.put(DbContract.LocationEntry.COLUMN_NAME_LAT, reminderLocation.getLatLng().latitude);
        vals.put(DbContract.LocationEntry.COLUMN_NAME_LONG, reminderLocation.getLatLng().longitude);
       // vals.put(DbContract.LocationEntry.COLUMN_NAME_REMINDER_ID, reminderLocation.getReminderId());
        vals.put(DbContract.LocationEntry.COLUMN_NAME_RADIUS, reminderLocation.getRadius());

        return vals;
    }

    @Override
    protected ReminderLocation buildObject(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(DbContract.LocationEntry._ID));
       // long reminderId = cursor.getLong(cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_REMINDER_ID));
        double lat = cursor.getDouble(cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_LAT));
        double longitude = cursor.getDouble(cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_LONG));

        int radiusIndex = cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME_RADIUS);
        int radius = cursor.getInt(radiusIndex);
        //return new ReminderLocation(id, reminderId, new LatLng(lat, longitude), radius);
        return null;
    }
}
