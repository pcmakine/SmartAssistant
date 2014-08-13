package com.touchdown.app.smartassistant.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Pete on 12.8.2014.
 */
public class Dao {
    public static final String LOG_TAG = Dao.class.getSimpleName();

    @Entity
    private SQLiteOpenHelper dbHelper;

    public Dao(SQLiteOpenHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    public void insert(){

        Class cls = this.getClass();
        Field[] fields = cls.getDeclaredFields();

        for(int i = 0; i  < fields.length; i++){
            Log.d(LOG_TAG, fields[i].toString());
            String name = fields[i].getName();
            Annotation[] annotations = fields[i].getAnnotations();
            Log.d(LOG_TAG, "number of annotations" + annotations.length);
            for (int j = 0; j < annotations.length; j++){
                String annotation = annotations[j].toString();
                Log.d(LOG_TAG, annotation);
            }
        }
    }

    private ContentValues values() throws NoSuchMethodException {
        Field[] fields = getFields();

        ContentValues vals = new ContentValues();
        for (int i = 0; i < fields.length; i++){
            Field field = fields[i];
            if(field.getType() == boolean.class){
                Class<?> c = this.getClass();
                Object object = null;

                try {
                    Constructor<?> cons = c.getConstructor();
                    object = cons.newInstance();
                    int truthAsInt = (Boolean) field.get(object) ? 1: 0;
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

            }
        }
        return vals;
    }

    private Field[] getFields(){
        Class cls = this.getClass();
        Field[] fields = cls.getDeclaredFields();
        return fields;
    }
}
