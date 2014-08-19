package com.touchdown.app.smartassistant.test;

import android.test.AndroidTestCase;

import com.google.android.gms.maps.model.LatLng;
import com.touchdown.app.smartassistant.Util;
import com.touchdown.app.smartassistant.data.DbContract;
import com.touchdown.app.smartassistant.data.DbHelper;
import com.touchdown.app.smartassistant.newdb.LocDao;
import com.touchdown.app.smartassistant.newdb.Trigger;
import com.touchdown.app.smartassistant.newdb.TriggerDao;
import com.touchdown.app.smartassistant.newdb.TriggerLocation;

/**
 * Created by Pete on 18.8.2014.
 */
public class TestTriggerDb extends AndroidTestCase {
    private static final double TEST_LAT = 60;
    private static final double TEST_LONG = 20;

    private DbHelper dbHelper;

    @Override
    public void setUp(){
        this.dbHelper = new DbHelper(mContext);
        Util.clearDb(dbHelper, mContext);
    }

    public void testInsertLocation(){
        LocDao locationDao = new LocDao(dbHelper, DbContract.LocationEntry.TABLE_NAME, DbContract.LocationEntry._ID);
        TriggerLocation location = new TriggerLocation(-1, new LatLng(TEST_LAT, TEST_LONG), TriggerLocation.DEFAULT_RADIUS, -1);

        long id = locationDao.insert(location);

        assertTrue(id != -1);
        assertTrue(id == 1);
    }

    public void testInsertTrigger(){
        TriggerDao triggerDao = new TriggerDao(dbHelper, DbContract.TriggerEntry.TABLE_NAME, DbContract.TriggerEntry._ID);
        Trigger trigger = new Trigger(-1, 0);
        long id = triggerDao.insert(trigger);

        assertTrue(id != -1);
    }

}
