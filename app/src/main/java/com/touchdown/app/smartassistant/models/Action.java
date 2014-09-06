package com.touchdown.app.smartassistant.models;

import android.os.Parcel;

/**
 * Created by Pete on 19.8.2014.
 */
public abstract class Action extends Data {
    private ActionType type;
    private boolean isOn;   //property of the subtables
    private long taskId;

    public Action(long id, ActionType type, boolean isOn, long taskId){
        super(id);
        this.type = type;
        this.isOn = isOn;
        this.taskId = taskId;
    }

    public ActionType getType(){
        return type;
    }

    protected Action(){

    }

    public void setType(ActionType type) {
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

    public void setOn(boolean on){
        isOn = on;
    }

    public long getTaskId(){
        return taskId;
    }
    public void setTaskId(long actionCollectionId){
        this.taskId = actionCollectionId;
    }

    public abstract void execute();

    public void writeToParcel(Parcel dest, int flags){
        dest.writeLong(getId());
        dest.writeLong(taskId);
        dest.writeInt(type.value);
        dest.writeBooleanArray(new boolean[]{isOn});
    }

    //todo check somewhere that this is a good way to implement equals
    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Action)){
            return false;
        }
        Action action = (Action) obj;

        return getId() == action.getId() && type == action.getType();
    }

    @Override
    public int hashCode() {
        int constant = 7;
        int hash = (int) getId() + constant;
        return hash;
    }

}
