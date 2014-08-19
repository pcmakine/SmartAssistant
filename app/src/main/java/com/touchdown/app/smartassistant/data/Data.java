package com.touchdown.app.smartassistant.data;

/**
 * Created by Pete on 18.8.2014.
 */
public abstract class Data {
    private long id;

    public Data(long id){
        this.id = id;
    }

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }
}
