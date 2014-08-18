package com.touchdown.app.smartassistant.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

        long id = db.insert(tableName, null, values);

        return id;
    }

    public int update(Object object, String tableName){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues vals = extractor.getContentValues(object);
        long id = extractor.getId(object);

        int numOfRowsAffected = 0;


        numOfRowsAffected = db.update(tableName,
                vals,
                DbContract.ReminderEntry._ID + " = ?",
                new String[] {String.valueOf(id)});
        return numOfRowsAffected;
    }

    public int remove(long id, String tableName){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(tableName, DbContract.ReminderEntry._ID + " =?",
                new String[] {id+""});
        db.close();
        return rowsAffected;
    }
}
