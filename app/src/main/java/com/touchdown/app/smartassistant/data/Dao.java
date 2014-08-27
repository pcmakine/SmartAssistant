package com.touchdown.app.smartassistant.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pete on 21.8.2014.
 */
public abstract class Dao<T> {

    protected SQLiteOpenHelper dbHelper;

    public Dao(SQLiteOpenHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    public T getOne(long id, String tableName, String idColumn){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(tableName, null,
                idColumn + " = ?",
                new String[] {String.valueOf(id)}, null, null, null, null);
        if(cursor != null && cursor.moveToFirst()){
        }else{
            return null;
        }
        T object = buildObject(cursor);
        return object;
    }


    public Cursor getAll(String tableName, String idColumn){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sortOrder = idColumn + " ASC";

        Cursor c = db.query(
                tableName,
                null, //reads all the fields
                null,
                null,
                null,
                null,
                sortOrder);
        //todo can the db be closed at this point?
        return c;
    }


    public List<T> findByFieldInt(String tableName, String columnName, int value){
        Cursor cursor = getDataByFieldInt(tableName, columnName, value);
        List<T> list = getListFromCursor(cursor);
        return list;
    }

    public Cursor getDataByFieldInt(String tableName, String columnName, int value){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(tableName, null,
                columnName + " = ?",
                new String[] {String.valueOf(value)}, null, null, null, null);
        return cursor;
    }

    public List<T> getAllAsList(String tableName, String idColumn){
        Cursor cursor = getAll(tableName, idColumn);
        List<T> list = getListFromCursor(cursor);
        return list;
    }

    private List<T> getListFromCursor(Cursor cursor){
        List<T> list = new ArrayList<T>();
        if(cursor != null){
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                list.add(buildObject(cursor));
                cursor.moveToNext();
            }
        }
        return list;
    }

    protected abstract T buildObject(Cursor cursor);
}
