package com.touchdown.app.smartassistant.models;

import android.content.ContentValues;

import com.touchdown.app.smartassistant.data.Data;
import com.touchdown.app.smartassistant.data.DbContract;
import com.touchdown.app.smartassistant.views.NotificationReminder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Pete on 19.8.2014.
 */
public class Task extends Data implements Comparable<Task>{
    private static final String TABLE_NAME = DbContract.TaskEntry.TABLE_NAME;
    private static final String ID_COLUMN = DbContract.TaskEntry._ID;

    private String name;
    private Trigger trigger;
    private Set<Action> actions;

    public Task(long id, String name, Trigger trigger, Action action) {
        super(id);
        this.trigger = trigger;
        this.actions = new HashSet<Action>();
        actions.add(action);
        this.name = name;
        setTableName(TABLE_NAME);
        setIdColumn(ID_COLUMN);
    }

    public void addAction(Action action){
        actions.add(action);
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
           trigger.setActionId(id);
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

    public NotificationReminder getAlarm(){
        for(Action action: actions){
            if(action.getType() == 0){
                return (NotificationReminder) action;
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
    public ContentValues getContentValues() {
        ContentValues vals = new ContentValues();
        vals.put(DbContract.TaskEntry.COLUMN_NAME_TASK_NAME, this.name);
        return vals;
    }
}
