package com.touchdown.app.smartassistant.test;

import android.test.AndroidTestCase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
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


    public void testEmptyReminderNotInserted(){

        DbHelper dbHelper = new DbHelper(mContext);

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
        DbHelper dbHelper = new DbHelper(mContext);

        ReminderDao reminder = new ReminderDao(-1, text, new LocationDao(-1, -1, loc));

        long rowId = reminder.insert(dbHelper);
        return rowId;
    }
}
