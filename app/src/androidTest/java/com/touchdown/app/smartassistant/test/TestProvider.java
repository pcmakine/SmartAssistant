package com.touchdown.app.smartassistant.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.touchdown.app.smartassistant.data.DbContract;
import com.touchdown.app.smartassistant.data.DbContract.ReminderEntry;
import com.touchdown.app.smartassistant.data.DbContract.LocationEntry;
import com.touchdown.app.smartassistant.data.DbHelper;
import com.touchdown.app.smartassistant.models.LocationDao;
import com.touchdown.app.smartassistant.models.Reminder;
import com.touchdown.app.smartassistant.models.ReminderDao;

import java.util.Map;
import java.util.Set;

/**
 * Created by Pete on 14.8.2014.
 */
public class TestProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    ReminderDao reminderManager;

    @Override
    public void setUp(){
        reminderManager = new ReminderDao(new DbHelper(mContext));
    }

    public void testDeleteDb() throws Throwable {
        mContext.deleteDatabase(DbHelper.DATABASE_NAME);
    }

    public void testInsertReadDb() {

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String reminderContent = "test reminder";

        Reminder reminder = new Reminder(-1, reminderContent, null);
        reminder.turnOn();

        ContentValues vals = reminderManager.values(reminder);

        long reminderId;
        reminderId = db.insert(DbContract.ReminderEntry.TABLE_NAME, null, vals);

        assertTrue(reminderId != -1);
        Log.d(LOG_TAG, "New row id: " + reminderId);

        Cursor cursor = getCursor(db, DbContract.ReminderEntry.TABLE_NAME);

        if(cursor.moveToFirst()){
            validateCursor(vals, cursor);
            int idIndex = cursor.getColumnIndex(DbContract.ReminderEntry._ID);
            long id = cursor.getLong(idIndex);
            assertEquals(1, id);

        }else{
            fail("No values for reminder returned");
        }

        LocationDao location = new LocationDao(-1, reminderId, new LatLng(TestDb.TESTLAT, TestDb.TESTLONG), 100);
        long rowId = location.insert(dbHelper);
        vals = location.values();

        assertTrue(rowId != -1);

        cursor = getCursor(db, DbContract.LocationEntry.TABLE_NAME);

        if(cursor.moveToFirst()){
            validateCursor(vals, cursor);

        }else{
            fail("No values for location returned");
        }
    }

    private Cursor getCursor(SQLiteDatabase db, String tableName){
        Cursor cursor = db.query(
                tableName,
                null,
                null,   //columns for the where clause
                null,   //values for the where clause
                null,   //columns to group by
                null,   //columns to filter by row groups
                null    //sort order
        );
        return cursor;
    }

    private void validateCursor(ContentValues expectedVals, Cursor valueCursor){
        Set<Map.Entry<String, Object>> valueSet = expectedVals.valueSet();
        for(Map.Entry<String, Object> entry : valueSet){
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(-1 == idx);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }

    }

    public void testGetType() {
        // content://com.example.android.sunshine.app/weather/
        String type = mContext.getContentResolver().getType(DbContract.ReminderEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals(DbContract.ReminderEntry.CONTENT_TYPE, type);


        // content://com.example.android.sunshine.app/location/
        type = mContext.getContentResolver().getType(DbContract.LocationEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/location
        assertEquals(LocationEntry.CONTENT_TYPE, type);

        // content://com.example.android.sunshine.app/location/1
        type = mContext.getContentResolver().getType(LocationEntry.buildLocationUri(1L));
        // vnd.android.cursor.item/com.example.android.sunshine.app/location
        assertEquals(LocationEntry.CONTENT_ITEM_TYPE, type);
    }
}
