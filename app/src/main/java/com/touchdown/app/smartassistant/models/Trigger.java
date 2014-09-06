package com.touchdown.app.smartassistant.models;

import android.os.Parcelable;


/**
 * Created by Pete on 18.8.2014.
 */
public abstract class Trigger extends Data implements Parcelable {
    private int type;
    private long taskId;

    public Trigger(long id, int type, long taskId) {
        super(id);
        this.type = type;
        this.taskId = taskId;
    }

    protected Trigger(){

    }

    public int getType(){
        return type;
    }

    public void setType(int type){
        this.type = type;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId){
        this.taskId = taskId;
    }


}
