package com.touchdown.app.smartassistant.data;

import android.content.ContentValues;

/**
 * Created by Pete on 18.8.2014.
 */
public abstract class Data {
    private long id;
    private String tableName;
    private String idColumn;

    public Data(long id){
        this.id = id;
    }

    public String getTableName(){
        return tableName;
    }

    public void setTableName(String tableName){
        this.tableName = tableName;
    }

    public String getIdColumn(){
        return this.idColumn;
    }

    public void setIdColumn(String idColumn){
        this.idColumn = idColumn;
    }

    public abstract ContentValues getContentValues();

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }
}
