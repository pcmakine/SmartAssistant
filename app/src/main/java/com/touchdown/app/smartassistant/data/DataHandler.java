package com.touchdown.app.smartassistant.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.touchdown.app.smartassistant.Util;
import com.touchdown.app.smartassistant.models.LocationDao;
import com.touchdown.app.smartassistant.models.Reminder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Pete on 17.8.2014.
 */
public class DataHandler {

    public static final String LOG_TAG = DataHandler.class.getSimpleName();

    private SQLiteOpenHelper dbHelper;
    private FieldExtractor extractor;


    public DataHandler(SQLiteOpenHelper dbHelper){
        this.dbHelper = dbHelper;
        extractor = new FieldExtractor();
    }

    public long insert(Object object, String tableName){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = extractor.getContentValues(object);
        long id = -1;
        id = db.insert(tableName, null, values);

        return id;
    }

    public int update(Object object, String tableName, Class tableClass){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues vals = extractor.getContentValues(object);
        long id = extractor.getId(object);

        int numOfRowsAffected = 0;

        numOfRowsAffected = db.update(tableName,
                vals,
                "_id = ?",
                new String[] {String.valueOf(id)});
        return numOfRowsAffected;
    }
}
