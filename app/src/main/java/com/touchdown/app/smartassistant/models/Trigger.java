package com.touchdown.app.smartassistant.models;

import android.content.ContentValues;

import com.touchdown.app.smartassistant.data.Data;

import java.io.Serializable;


/**
 * Created by Pete on 18.8.2014.
 */
public abstract class Trigger extends Data {
    private int type;
    private long actionId;

    public Trigger(long id, int type, long actionId) {
        super(id);
        this.type = type;
        this.actionId = actionId;
    }

    public int getType(){
        return type;
    }

    public long getActionId() {
        return actionId;
    }

    public void setActionId(long actionId){
        this.actionId = actionId;
    }


}
