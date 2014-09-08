package com.touchdown.app.smartassistant.models;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.touchdown.app.smartassistant.data.DbContract;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Pete on 19.8.2014.
 */
public class Task extends Data implements Comparable<Task>, Parcelable{
    private static final String TABLE_NAME = DbContract.TaskEntry.TABLE_NAME;
    private static final String ID_COLUMN = DbContract.TaskEntry._ID;

    private String name;
    private Trigger trigger;
    private Set<Action> actions;

    public Task(long id, String name, Trigger trigger) {
        super(id);
        this.trigger = trigger;
        this.actions = new HashSet<Action>();
        this.name = name;
    }

    public void addAction(Action action){
        if(action != null){
            actions.add(action);
        }
    }

    public void removeAction(Action action){
        actions.remove(action);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Action> getActions(){
        List<Action> actionList = new ArrayList<Action>();
        actionList.addAll(actions);
        return actionList;
    }

    public Trigger getTrigger(){
        return trigger;
    }

    public void setIdForThisAndChildObjects(long id){
        setId(id);
        if(trigger != null){
           trigger.setTaskId(id);
        }
        if(actions != null){
            for(Action action: actions){
                action.setTaskId(id);
            }
        }
    }

    public boolean isActive(){
        if(actions!= null && !actions.isEmpty()){
            for(Action action: actions){
                if(action.isOn()){
                    return true;
                }
            }
        }
        return false;
    }

    public void turnAllActionsOff(){
        if(actions!= null && !actions.isEmpty()){
            for(Action action: actions){
                action.turnOff();
            }
        }
    }

    public void executeActions() {
        if(actions != null){
            for(Action action: actions){
                action.execute();
            }
        }
    }

    public Alarm getAlarm(){
        for(Action action: actions){
            if(action.getType() == ActionType.ALARM){
                return (Alarm) action;
            }
        }
        return null;
    }

    public RingerVolume getRingerVolume(){
        for(Action action: actions){
            if(action.getType() == ActionType.RINGERVOLUME){
                return (RingerVolume) action;
            }
        }
        return null;
    }

    public TriggerLocation getLocation(){
        return (TriggerLocation) trigger;
    }

    public void setLocation(TriggerLocation location){
        this.trigger = location;
    }

    //todo check somewhere that this is a good way to implement equals
    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Task)){
            return false;
        }
        Task task = (Task) obj;

        return getId() == task.getId() && name.equals(task.getName());
    }

    @Override
    public int hashCode() {
        int constant = 7;
        int hash = name.hashCode() + constant;
        return hash;
    }

    @Override
    public int compareTo(Task another) {
        if(getId() < another.getId()){
            return -1;
        }else if(getId() > another.getId()){
            return 1;
        }
        return 0;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getIdColumn() {
        return ID_COLUMN;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues vals = new ContentValues();
        vals.put(DbContract.TaskEntry.COLUMN_NAME_TASK_NAME, this.name);
        return vals;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //superclass
        dest.writeLong(getId());

        //this class
        dest.writeString(name);
        dest.writeParcelable(trigger, 0);
        actionsToParcel(dest, flags);
    }

    private void actionsToParcel(Parcel dest, int flags){
        dest.writeInt(actions.size());
        for (Action action: actions){
            dest.writeParcelable(action, 0);
        }
    }

    public static final Parcelable.Creator<Task> CREATOR
            = new Parcelable.Creator<Task>() {
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    private Task(Parcel in) {
        //superclass
        setId(in.readLong());

        name = in.readString();
        trigger = in.readParcelable(TriggerLocation.class.getClassLoader());

        int numberOfActions = in.readInt();
        retrieveActions(in, numberOfActions);
    }

    private void retrieveActions(Parcel in, int numberOfActions){
        actions = new HashSet<Action>();
        for (int i = 0; i < numberOfActions; i++){
            Action action = (Action) in.readParcelable(Action.class.getClassLoader());
            actions.add(action);
        }
    }

    @Override
    public String toString(){
        return name;
    }
}
