package com.touchdown.app.smartassistant.test;

import android.database.Cursor;
import android.test.AndroidTestCase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.touchdown.app.smartassistant.Util;
import com.touchdown.app.smartassistant.data.DbHelper;
import com.touchdown.app.smartassistant.models.ReminderLocation;
import com.touchdown.app.smartassistant.models.Reminder;
import com.touchdown.app.smartassistant.services.ReminderManager;

/**
 * Created by Pete on 4.8.2014.
 */
public class TestReminderDao extends AndroidTestCase {
    public static final String LOG_TAG = TestReminderDao.class.getSimpleName();

    public static final double TESTLAT = 64.772;
    public static final double TESTLONG = -147.335;
    DbHelper dbHelper;
    ReminderManager reminderManager;


    @Override
    public void setUp(){
        reminderManager = ReminderManager.getInstance(mContext);
        Util.clearDb(dbHelper, mContext);
    }

    public void testEmptyReminderNotInserted(){

        Reminder reminder = new Reminder(-1, "", null);

        long rowId = reminderManager.insert(reminder);

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

        Reminder reminder = reminderManager.getOne(firstId);

        assertEquals("test reminder", reminder.getContent());
        assertEquals(testlatlng, reminder.getReminderLocation().getLatLng());
        assertEquals(firstId, reminder.getId());
        assertEquals(firstId, reminder.getReminderLocation().getReminderId());

        reminder = reminderManager.getOne(secondId);

        assertEquals("test reminder 2", reminder.getContent());
        assertEquals(new LatLng(4, 9), reminder.getReminderLocation().getLatLng());
        assertEquals(secondId, reminder.getReminderLocation().getReminderId());
    }

    private long insertTestReminder(String text, LatLng loc){

        Reminder reminder = new Reminder(-1, text, new ReminderLocation(-1, -1, loc, 50));

        long rowId = reminderManager.insert(reminder);
        return rowId;
    }

    public void testGetAllReturnsCorrectNumberofReminders(){
        Util.insertTestData(mContext, Util.TEST_REMINDER_DEFAULT_COUNT);
        Cursor c = reminderManager.getAllReminderData();
        assertEquals(Util.TEST_REMINDER_DEFAULT_COUNT, c.getCount());
    }

    public void testGetOne(){
        Util.insertTestData(mContext, 4);
        Reminder reminder = reminderManager.getOne(3);

        assertEquals(reminder.getId(), 3);
    }

    public void testRemoveReminder(){
        Util.insertTestData(mContext, 5);
        Reminder reminder = reminderManager.getOne(4);

        assertFalse(reminder == null);

        int rowsAffected = reminderManager.remove(4);

        assertEquals(rowsAffected, 1);

        reminder = reminderManager.getOne(4);
        assertTrue(reminder == null);
    }

    public void testOnStatusSavedAndRetrievedCorrectly(){
        long testId = 3;

        Util.insertTestData(mContext, 5);
        Reminder reminder = reminderManager.getOne(testId);
        reminder.setOn(true);

        reminderManager.update(reminder);

        reminder = reminderManager.getOne(testId);

        assertTrue(reminder != null);
        assertEquals(true, reminder.isOn());

    }


}
