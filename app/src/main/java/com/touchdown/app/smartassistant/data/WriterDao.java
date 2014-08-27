package com.touchdown.app.smartassistant.data;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.touchdown.app.smartassistant.data.Data;

/**
 * Created by Pete on 21.8.2014.
 */
public class WriterDao {
    private SQLiteOpenHelper dbHelper;

    public WriterDao(SQLiteOpenHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    public long insert(Data data){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(data.getTableName(), null, data.getContentValues());
        db.close();
        return id;
    }

    public int update(Data data){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int numOfRowsAffected = db.update(data.getTableName(), data.getContentValues(), data.getIdColumn() + " = ?",
                new String[] {String.valueOf(data.getId())});

        db.close();
        return numOfRowsAffected;
    }

    public int remove(long id, String tableName, String idColumn){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(tableName, idColumn + " =?",
                new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected;
    }
}
