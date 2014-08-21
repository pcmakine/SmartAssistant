package com.touchdown.app.smartassistant.newdb;

import android.content.ContentValues;

import com.touchdown.app.smartassistant.data.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pete on 19.8.2014.
 */
public abstract class Action extends Data {
    private int type;
    private boolean isOn;   //property of the subtables
    private long actionCollectionId;

    public Action(long id, int type, boolean isOn, long actionCollectionId){
        super(id);
        this.type = type;
        this.isOn = isOn;
        this.actionCollectionId = actionCollectionId;
    }

    public int getType(){
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isOn(){
        return isOn;
    }

    public void turnOn(){
        isOn = true;
    }

    public void turnOff(){
        isOn = false;
    }

    public long getActionCollectionId(){
        return actionCollectionId;
    }

    public void setTaskId(long actionCollectionId){
        this.actionCollectionId = actionCollectionId;
    }
}
