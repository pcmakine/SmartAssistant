package com.touchdown.app.smartassistant.test;

import android.os.Bundle;
import android.test.AndroidTestCase;

import com.google.android.gms.maps.model.LatLng;
import com.touchdown.app.smartassistant.models.ActionType;
import com.touchdown.app.smartassistant.models.Alarm;
import com.touchdown.app.smartassistant.models.RingerVolume;
import com.touchdown.app.smartassistant.models.Task;
import com.touchdown.app.smartassistant.models.TriggerLocation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by Pete on 6.9.2014.
 */
public class TestParcelable extends AndroidTestCase {

    public void testReadWriteLocation(){
        int fieldsWrittenToParcel = 8;

        TriggerLocation location = new TriggerLocation(2,
                new LatLng(TestDatabase.TEST_LAT, TestDatabase.TEST_LONG),
                TriggerLocation.DEFAULT_RADIUS,
                5);

        location.setPending(true);
        location.turnOnDepartureTrigger();


        Bundle bundle = new Bundle();
        bundle.putParcelable("location", location);

        TriggerLocation readLocation = bundle.getParcelable("location");

        assertEquals(location.getLatLng(), readLocation.getLatLng());
        assertEquals(location.getId(), readLocation.getId());
        assertEquals(location.getTaskId(), readLocation.getTaskId());
        assertEquals(location.getRadius(), readLocation.getRadius());
        assertEquals(location.isArrivalTriggerOn(), readLocation.isArrivalTriggerOn());
        assertEquals(location.isDepartureTriggerOn(), readLocation.isDepartureTriggerOn());
        assertEquals(location.isPending(), readLocation.isPending());
        assertEquals(location.getType(), readLocation.getType());

        assertEquals(countObjectAndItsSuperclassNonStaticFields(location), fieldsWrittenToParcel);
    }

    public void testReadWriteRingerVolume(){
        int fieldsWrittenToParcel = 5;

        RingerVolume volume = new RingerVolume(5, ActionType.RINGERVOLUME, true, 2);

        volume.setVolume(77);

        Bundle bundle = new Bundle();

        bundle.putParcelable("vol", volume);

        RingerVolume readVolume = bundle.getParcelable("vol");

        assertEquals(volume.getId(), readVolume.getId());
        assertEquals(readVolume.getType(), ActionType.RINGERVOLUME);
        assertEquals(volume.isOn(), readVolume.isOn());
        assertEquals(volume.getTaskId(), readVolume.getTaskId());
        assertEquals(volume.getVolume(), readVolume.getVolume());

        assertEquals(fieldsWrittenToParcel, countObjectAndItsSuperclassNonStaticFields(volume));
    }

    public void testReadWriteAlarm(){
        int fieldsWrittenToParcel = 7;

        Alarm alarm = new Alarm(5, ActionType.ALARM, "al", true, 2);

        alarm.enableFullScreen(true);

        Bundle bundle = new Bundle();

        bundle.putParcelable("alarm", alarm);

        Alarm readAlarm = bundle.getParcelable("alarm");

        assertEquals(alarm.getId(), readAlarm.getId());
        assertEquals(alarm.getType(), ActionType.ALARM);
        assertEquals(alarm.isOn(), readAlarm.isOn());
        assertEquals(alarm.getTaskId(), readAlarm.getTaskId());
        assertEquals(true, alarm.isFullScreenEnabled());
        assertEquals(alarm.isNotificationEnabled(), readAlarm.isNotificationEnabled());

        assertEquals(fieldsWrittenToParcel, countObjectAndItsSuperclassNonStaticFields(alarm));
    }

    public void testReadWriteTask(){

        int fieldsWrittenToParcel = 4;

        TriggerLocation location = TriggerLocation.createDefault(new LatLng(TestDatabase.TEST_LAT, TestDatabase.TEST_LONG));
        Alarm alarm = Alarm.createDefault();

        Task task = new Task(5, "testTask", location);
        task.addAction(alarm);

        task.setIdForThisAndChildObjects(5);

        Bundle bundle = new Bundle();

        bundle.putParcelable("task", task);

        Task readTask = bundle.getParcelable("task");

        assertEquals(task.getId(), readTask.getId());
        assertEquals(task.getId(), readTask.getAlarm().getTaskId());
        assertEquals(task.getLocation().getLatLng(), readTask.getLocation().getLatLng());

        assertEquals(fieldsWrittenToParcel, countObjectAndItsSuperclassNonStaticFields(task));

    }

    private int countObjectAndItsSuperclassNonStaticFields(Object object){
        int count = 0;
        Class cls = object.getClass();
        do{
            count = count + countNonStaticFields(cls);
            cls = cls.getSuperclass();
        }while(cls != null);

        return count;
    }

    private int countNonStaticFields(Class cls){
        Field[] fields = cls.getDeclaredFields();

        int count = 0;
        for (int i = 0; i < fields.length; i++) {
            if(!Modifier.isStatic(fields[i].getModifiers())){
                count++;
            }
        }
        return count;
    }
}
