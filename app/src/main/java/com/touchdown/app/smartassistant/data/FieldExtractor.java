package com.touchdown.app.smartassistant.data;

import android.content.ContentValues;
import android.util.Log;

import com.touchdown.app.smartassistant.Util;
import com.touchdown.app.smartassistant.models.ReminderLocation;
import com.touchdown.app.smartassistant.models.Reminder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Pete on 18.8.2014.
 */
public class FieldExtractor {
    private Object obj;
    private ColumnList reminderColumns;
    private ColumnList locationColumns;
    public static final String LOG_TAG = FieldExtractor.class.getSimpleName();

    public FieldExtractor(){
        reminderColumns = new ColumnList();
        reminderColumns.buildList(DbContract.ReminderEntry.class);
        locationColumns = new ColumnList();
        locationColumns.buildList(DbContract.LocationEntry.class);
    }

    public long getId(Object obj){
        this.obj = obj;
        Field idField = null;
        try {
            idField = obj.getClass().getDeclaredField("id");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        try {
            idField.setAccessible(true);
            return (Long) idField.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public ContentValues getContentValues(Object obj){
        this.obj = obj;
        List<Field> fieldsList = getFieldsSortedByName(obj.getClass());
        ContentValues vals = extractValuesFromFieldList(fieldsList, obj.getClass());
        resetColumnPointersToBeginning();
        return vals;
    }


    public List<Field> getFieldsSortedByName(Class cls){
        return sortFieldsByName(cls.getDeclaredFields());
    }

    private ContentValues extractValuesFromFieldList(List<Field> fieldsList, Class objectType){
        ContentValues vals = new ContentValues();
        for(Field field: fieldsList){
            if(noAnnotations(field)){
                if(objectType == Reminder.class){
                    addFieldToContentValues(field, vals, reminderColumns);
                }else if(objectType == ReminderLocation.class){
                    addFieldToContentValues(field, vals, locationColumns);
                }
            }
        }
        return vals;
    }

    private void addFieldToContentValues(Field field, ContentValues values, ColumnList columnNames){
        if(field.getName().equals("id") || (field.getModifiers() & Modifier.STATIC) == Modifier.STATIC){
            return;
        }
        if(field.getType() == boolean.class){
            putBoolean(field, values, columnNames.getCurrentAndAdvanceOne());
        }else if(field.getType() == String.class){
            putString(field, values, columnNames.getCurrentAndAdvanceOne());
        }else if(field.getType() == double.class){
            putDouble(field, values, columnNames.getCurrentAndAdvanceOne());
        }else if(field.getType() == long.class){
            putLong(field, values, columnNames.getCurrentAndAdvanceOne());
        }else if(field.getType() == int.class){
            putInt(field, values, columnNames.getCurrentAndAdvanceOne());
        }
    }


    private void putBoolean(Field field, ContentValues values, String columnName){
        field.setAccessible(true);
        try {
            int truthAsInt = Util.booleanAsInt((Boolean) field.get(obj));
            values.put(columnName, truthAsInt);
        }  catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void putString(Field field, ContentValues values, String columnName){
        field.setAccessible(true);
        try {
            String value = (String) field.get(obj);
            values.put(columnName, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void putDouble(Field field, ContentValues values, String columnName){
        field.setAccessible(true);
        try {
            double value = (Double) field.get(obj);
            values.put(columnName, value);
        }  catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void putLong(Field field, ContentValues values, String columnName){
        field.setAccessible(true);
        try {
            long value = (Long) field.get(obj);
            values.put(columnName, value);
        }  catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void putInt(Field field, ContentValues values, String columnName){
        field.setAccessible(true);
        try {
            int value = (Integer) field.get(obj);
            values.put(columnName, value);
        }  catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private boolean noAnnotations(Field field){
        Annotation[] annotations = field.getAnnotations();
        Log.d(LOG_TAG, "number of annotations" + annotations.length);
        return annotations.length == 0;
    }

    private List<Field> sortFieldsByName(Field[] fields){
        List<Field> fieldsList = Arrays.asList(fields);
        Collections.sort(fieldsList, new Comparator<Field>() {
            @Override
            public int compare(Field lhs, Field rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        return fieldsList;
    }

    private void resetColumnPointersToBeginning(){
        this.reminderColumns.resetPointerToBeginning();
        this.locationColumns.resetPointerToBeginning();
    }

    public ColumnList getReminderColumns(){
        return reminderColumns;
    }

    public ColumnList getLocationColumns(){
        return locationColumns;
    }

}
