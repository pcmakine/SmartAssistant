package com.touchdown.app.smartassistant.models;

import android.content.ContentValues;

/**
 * Created by Pete on 18.8.2014.
 */
public abstract class Data {
    private long id;

    public Data(long id){
        this.id = id;
    }

    protected Data(){

    }


    public abstract String getTableName();

    public abstract String getIdColumn();

    public abstract ContentValues getContentValues();

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }
}
