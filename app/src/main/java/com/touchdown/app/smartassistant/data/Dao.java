package com.touchdown.app.smartassistant.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pete on 18.8.2014.
 */
public abstract class Dao<T> {

    protected SQLiteOpenHelper dbHelper;
    protected String tableName;
    protected String idColumn;

    public Dao(SQLiteOpenHelper dbHelper, String tableName, String idColumn){
        this.dbHelper = dbHelper;
        this.tableName = tableName;
        this.idColumn = idColumn;
    }

    public long insert(T t){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(tableName, null, values(t));
        return id;
    }

    public int update(T t, long id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int numOfRowsAffected = db.update(tableName, values(t), idColumn + " = ?",
                new String[] {String.valueOf(id)});
        return numOfRowsAffected;
    }

    public int remove(long id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(tableName, idColumn + " =?",
                new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected;
    }

    public T getOne(long id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbContract.ReminderEntry.TABLE_NAME, null,
                DbContract.ReminderEntry._ID + " = ?",
                new String[] {String.valueOf(id)}, null, null, null, null);
        if(cursor != null && cursor.moveToFirst()){
        }else{
            return null;
        }
        return buildObject(cursor);
    }

    public Cursor getAll(){
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

    public List<T> getAllAsList(){
        Cursor cursor = getAll();
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

    protected abstract ContentValues values(T t);

    protected abstract T buildObject(Cursor data);
}
