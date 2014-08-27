package com.touchdown.app.smartassistant.test;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.touchdown.app.smartassistant.Util;
import com.touchdown.app.smartassistant.data.DbContract.LocationEntry;
import com.touchdown.app.smartassistant.data.DbContract.TaskEntry;
import com.touchdown.app.smartassistant.data.DbContract.ReminderEntry;

import com.touchdown.app.smartassistant.data.DbHelper;
import com.touchdown.app.smartassistant.views.NotificationReminder;
import com.touchdown.app.smartassistant.data.ActionReminderDao;
import com.touchdown.app.smartassistant.data.LocDao;
import com.touchdown.app.smartassistant.models.Task;
import com.touchdown.app.smartassistant.data.TaskDao;
import com.touchdown.app.smartassistant.services.TaskManager;
import com.touchdown.app.smartassistant.models.TriggerLocation;
import com.touchdown.app.smartassistant.data.WriterDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pete on 20.8.2014.
 */
public class TestDatabase extends AndroidTestCase {

    public static final String LOG_TAG = TestDatabase.class.getSimpleName();
    private static final double TEST_LAT = 60;
    private static final double TEST_LONG = 20;

    private SQLiteOpenHelper dbHelper;
    private TaskDao taskDao;
    private WriterDao wDao;
    private ActionReminderDao reminderDao;
    private LocDao locationDao;

    private TaskManager taskManager;

    @Override
    public void setUp(){
        taskManager = TaskManager.getInstance(mContext);
        this.dbHelper = DbHelper.getInstance(mContext);
        Util.clearDb(mContext, dbHelper);

        reminderDao = new ActionReminderDao(dbHelper);
        locationDao = new LocDao(dbHelper);
        taskDao = new TaskDao(dbHelper);
        wDao = new WriterDao(dbHelper);

    }

    public void testInsertActionDirectlyInTheDatabase(){
        NotificationReminder reminder = getDefaultTestReminder();
        TriggerLocation testLocation = getDefaultTestLocation();

        Task task = new Task(-1, "testTask", testLocation, reminder);

        long taskId = wDao.insert(task);
        reminder.setTaskId(taskId);
        long reminderId = wDao.insert(reminder);

        testLocation.setActionId(taskId);
        long locId = wDao.insert(testLocation);

        assertTrue(taskId != -1);
        assertTrue(reminderId != -1);
        assertTrue(locId != -1);
    }

    public void testInsertLocation(){

        long id = insertDefaultLocation();
        assertTrue(id != -1);
    }

    private long insertDefaultLocation(){

        TriggerLocation location = getDefaultTestLocation();

        List<Task> tasks = insertTwoTasks("f", "s");

        location.setActionId(tasks.get(0).getId());
        return wDao.insert(location);
    }

    public void testGetLocation(){
        long id = insertDefaultLocation();

        LocDao locationDao = new LocDao(dbHelper);
        TriggerLocation location = locationDao.getOne(id, LocationEntry.TABLE_NAME, LocationEntry._ID);

        assertEquals(id, location.getId());
        assertEquals(TriggerLocation.DEFAULT_RADIUS, location.getRadius());

        double lat = location.getLatLng().latitude;
        double longitude = location.getLatLng().longitude;

        assertEquals(lat, TEST_LAT);
        assertEquals(longitude, TEST_LONG);
    }

    public void testUpdateLocationLat(){
        long id = insertDefaultLocation();

        LocDao locationDao = new LocDao(dbHelper);

        TriggerLocation loc = locationDao.getOne(id, LocationEntry.TABLE_NAME, LocationEntry._ID);

        double updatedLat = 12;
        loc.setLatLng(new LatLng(updatedLat, TEST_LONG));

        wDao.update(loc);

        TriggerLocation updatedLoc = locationDao.getOne(id, LocationEntry.TABLE_NAME, LocationEntry._ID);

        assertEquals(id, updatedLoc.getId());
        assertEquals(TriggerLocation.DEFAULT_RADIUS, updatedLoc.getRadius());

        double lat = updatedLoc.getLatLng().latitude;
        double longitude = updatedLoc.getLatLng().longitude;

        assertEquals(lat, updatedLat);
        assertEquals(longitude, TEST_LONG);
    }

    public void testGetAllTasks(){
        insertTwoTasks("first test task", "second test task");
        List<Task> tasks = taskManager.getAllTasks();

        assertTrue(tasks.size() == 2);

        assertEquals("first test task", tasks.get(0).getName());
        assertEquals("second test task", tasks.get(1).getName());

        TriggerLocation location = (TriggerLocation) tasks.get(0).getTrigger();

        assertEquals(new LatLng(TEST_LAT, TEST_LONG), location.getLatLng());
    }

    private List<Task> insertTwoTasks(String firstName, String secondName){
        List<Task> list = new ArrayList<Task>();
        Task task = new Task(-1, firstName, getDefaultTestLocation(), getDefaultTestReminder());
        long id = taskManager.insert(task);

        task.setIdForThisAndChildObjects(id);

        list.add(task);
        assertTrue(id != -1);

        Task secondTask = new Task(-1, secondName, getDefaultTestLocation(), getDefaultTestReminder());
        id = taskManager.insert(secondTask);

        secondTask.setIdForThisAndChildObjects(id);
        list.add(secondTask);

        assertTrue(id != -1);

        return list;
    }

    public void testUpdateWorks(){
        Task task = new Task(-1, "original name", getDefaultTestLocation(), getDefaultTestReminder());

        long id = taskManager.insert(task);

        assertTrue(id != -1);

        task = taskManager.findTaskById(id);

        task.setName("modified name");

        TriggerLocation location = (TriggerLocation) task.getLocation();
        LatLng testLoc = new LatLng(90, 90);

        NotificationReminder reminder = (NotificationReminder) task.getActions().get(0);
        reminder.setContent("modified content");

        location.setLatLng(testLoc);

        taskManager.update(task);

        task = taskManager.findTaskById(id);

        assertEquals("modified name", task.getName());

        LatLng taskLoc = ((TriggerLocation) task.getTrigger()).getLatLng();
        assertEquals(taskLoc, testLoc);

        NotificationReminder modReminder = (NotificationReminder) task.getActions().get(0);

        assertEquals("modified content", modReminder.getContent());
    }

    public void testRemoveWorks(){
        List<Task> tasks = insertTwoTasks("first", "second");

        int rowsAffected = taskManager.removeTask(tasks.get(0).getId());

        Cursor cursor = taskManager.getAllTaskData();

        int numberOfRemindersInDb = reminderDao.getAll(ReminderEntry.TABLE_NAME,
                ReminderEntry._ID).getCount();
        int numberOfLocationsInDb = locationDao.getAll(LocationEntry.TABLE_NAME,
                LocationEntry._ID).getCount();

        assertTrue(rowsAffected == 1);
        assertTrue(cursor.getCount() == 1);

        assertTrue(numberOfRemindersInDb == 1);

        rowsAffected = taskManager.removeTask(tasks.get(1).getId());

        assertTrue(rowsAffected == 1);
        assertTrue(taskManager.getAllTaskData().getCount() == 0);

        numberOfRemindersInDb = reminderDao.getAll(ReminderEntry.TABLE_NAME,
                ReminderEntry._ID).getCount();

        numberOfLocationsInDb = locationDao.getAll(LocationEntry.TABLE_NAME,
                LocationEntry._ID).getCount();

        DbHelper.getInstance(mContext).close();

        assertTrue(numberOfRemindersInDb == 0);
        assertTrue(numberOfLocationsInDb == 0);
    }

    public void testGetActiveTaskCount(){
        insertTwoTasks("first", "second");

        assertEquals(2, taskManager.getActiveTaskCount());
    }

    public void testGetAllDataDirectlyFromDb(){
        insertTwoTasks("1st task", "2nd task");

        Cursor cursor = taskDao.getAll(TaskEntry.TABLE_NAME, TaskEntry._ID);
        int count = cursor.getCount();

        DbHelper.getInstance(mContext).close();
        assertTrue(count == 2);
    }

    public void testGetAllTaskDataUsingTaskManager(){

        insertTwoTasks("3rd task", "4th task");

        Cursor cursor = taskManager.getAllTaskData();
        int count = cursor.getCount();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){

            String name = cursor.getString(cursor.getColumnIndex(TaskEntry.COLUMN_NAME_TASK_NAME));
            int s = 5;
            cursor.moveToNext();
        }

        DbHelper.getInstance(mContext).close();

        assertTrue(count == 2);
    }

    public void testGetAllTasksWithLocationTrigger(){
        Util.insertTestData(mContext, 2);
        String locationIdAlias = "location";
        String reminderIdAlias = "reminder";

        String query = "SELECT * FROM " + TaskEntry.TABLE_NAME + " JOIN " +
                LocationEntry.TABLE_NAME + " ON " +
                TaskEntry.TABLE_NAME + "." + TaskEntry._ID + " = " +
                LocationEntry.TABLE_NAME + "." + LocationEntry._ID +
                " JOIN " + ReminderEntry.TABLE_NAME + " ON " +
                LocationEntry.TABLE_NAME + "." + LocationEntry.COLUMN_NAME_TASK_ID + " = " +
                ReminderEntry.TABLE_NAME + "." + ReminderEntry.COLUMN_NAME_TASK_ID;


        SQLiteDatabase db = DbHelper.getInstance(mContext).getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        int resultSetColumnCount = cursor.getColumnCount();

        while(!cursor.isAfterLast()){

            String[] columns = cursor.getColumnNames();

            for (int i = 0; i < columns.length; i++){
                Log.d(LOG_TAG, columns[i]);
            }
            cursor.moveToNext();
        }

        int trueColumnCount = getTableColumnCount(TaskEntry.TABLE_NAME) +
                getTableColumnCount(LocationEntry.TABLE_NAME) +
                getTableColumnCount(ReminderEntry.TABLE_NAME);

        Log.d(LOG_TAG, "result set column count: " + resultSetColumnCount);
        Log.d(LOG_TAG, "true column count: " + trueColumnCount);

        db.close();

        assertTrue(resultSetColumnCount == trueColumnCount);

    }

    public void testGetAllTasksWithLocationTriggerWithTaskManager(){
        Util.insertTestData(mContext, 3);
        List<Task> tasks = taskManager.getAllTasksWithLocationTrigger();

        assertEquals(3, tasks.size());
    }

    private int getTableColumnCount(String tableName){
        SQLiteDatabase database = DbHelper.getInstance(mContext).getReadableDatabase();
        Cursor cursor = database.query(
                tableName,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        return cursor.getColumnCount();
    }

    private NotificationReminder getDefaultTestReminder(){
        return new NotificationReminder(-1, 0, "test reminder content", true, -1);
    }

    private TriggerLocation getDefaultTestLocation(){
        return new TriggerLocation(-1, new LatLng(TEST_LAT, TEST_LONG), TriggerLocation.DEFAULT_RADIUS, -1);
    }
}
