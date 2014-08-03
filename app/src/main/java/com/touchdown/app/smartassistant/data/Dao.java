package com.touchdown.app.smartassistant.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Pete on 3.8.2014.
 */
public interface Dao {

    public long insert(SQLiteOpenHelper dbHelper);
    public Cursor getAll(SQLiteOpenHelper dbHelper);
    public Dao getOne(SQLiteOpenHelper dbHelper, long id);
    public int remove(SQLiteOpenHelper dbHelper, long id);


}
