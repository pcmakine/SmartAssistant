package com.touchdown.app.smartassistant.test;

import android.database.Cursor;
import android.test.AndroidTestCase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.touchdown.app.smartassistant.Util;
import com.touchdown.app.smartassistant.data.DbHelper;
import com.touchdown.app.smartassistant.models.LocationDao;
import com.touchdown.app.smartassistant.models.ReminderDao;

/**
 * Created by Pete on 4.8.2014.
 */
public class TestReminderDao extends AndroidTestCase {
    public static final String LOG_TAG = TestReminderDao.class.getSimpleName();

    public static final double TESTLAT = 64.772;
    public static final double TESTLONG = -147.335;
    DbHelper dbHelper;


    @Override
    public void setUp(){
        dbHelper = new DbHelper(mContext);
        Util.clearDb(dbHelper, mContext);
    }

    public void testEmptyReminderNotInserted(){

        ReminderDao reminder = new ReminderDao(-1, "", null);

        long rowId = reminder.insert(dbHelper);

        assertTrue(rowId == -1);
    }

    public void testNonEmptyReminderInserted(){
        long rowId = insertTestReminder("test reminder", new LatLng(TESTLAT, TESTLONG));
        assertFalse(rowId == -1);
    }

    public void testDbReturnsCorrectReminderAndLocation(){
        LatLng testlatlng = new LatLng(TESTLAT, TESTLONG);
        long firstId = insertTestReminder("test reminder", testlatlng);

        Log.d(LOG_TAG + "FIRST ID", String.valueOf(firstId));
        long secondId = insertTestReminder("test reminder 2", new LatLng(4, 9));

        ReminderDao reminder = ReminderDao.getOne(new DbHelper(mContext), firstId);

        assertEquals("test reminder", reminder.getContent());
        assertEquals(testlatlng, reminder.getLocation().getLatLng());
        assertEquals(firstId, reminder.getId());
        assertEquals(firstId, reminder.getLocation().getReminderId());

        reminder = ReminderDao.getOne(new DbHelper(mContext), secondId);

        assertEquals("test reminder 2", reminder.getContent());
        assertEquals(new LatLng(4, 9), reminder.getLocation().getLatLng());
        assertEquals(secondId, reminder.getLocation().getReminderId());
    }

    private long insertTestReminder(String text, LatLng loc){

        ReminderDao reminder = new ReminderDao(-1, text, new LocationDao(-1, -1, loc, 50));

        long rowId = reminder.insert(dbHelper);
        return rowId;
    }

    public void testGetAllReturnsCorrectNumberofReminders(){
        Util.insertTestData(dbHelper, Util.TEST_REMINDER_DEFAULT_COUNT);
        Cursor c = ReminderDao.getAll(dbHelper);
        assertEquals(Util.TEST_REMINDER_DEFAULT_COUNT, c.getCount());
    }

    public void testGetOne(){
        Util.insertTestData(dbHelper, 4);
        ReminderDao reminder = ReminderDao.getOne(dbHelper, 3);

        assertEquals(reminder.getId(), 3);
    }

    public void testRemoveReminder(){
        Util.insertTestData(dbHelper, 5);
        ReminderDao reminder = ReminderDao.getOne(dbHelper, 4);

        assertFalse(reminder == null);

        int rowsAffected = ReminderDao.remove(dbHelper, 4);

        assertEquals(rowsAffected, 1);

        reminder = ReminderDao.getOne(dbHelper, 4);
        assertTrue(reminder == null);
    }


}
