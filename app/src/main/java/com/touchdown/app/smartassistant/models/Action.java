package com.touchdown.app.smartassistant.models;

import com.touchdown.app.smartassistant.data.Data;

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

    public abstract void execute();

    //todo check somewhere that this is a good way to implement equals
    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Action)){
            return false;
        }
        Action action = (Action) obj;

        return getId() == action.getId();
    }

    @Override
    public int hashCode() {
        int constant = 7;
        int hash = (int) getId() + constant;
        return hash;
    }

}
