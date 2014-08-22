package com.touchdown.app.smartassistant.newdb;

import android.content.ContentValues;

import com.touchdown.app.smartassistant.data.Data;
import com.touchdown.app.smartassistant.data.DbContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pete on 19.8.2014.
 */
public class Task extends Data implements Comparable<Task>{
    private static final String TABLE_NAME = DbContract.TaskEntry.TABLE_NAME;
    private static final String ID_COLUMN = DbContract.TaskEntry._ID;

    private String name;
    private Trigger trigger;
    private List<Action> actions;

    public Task(long id, String name, Trigger trigger, Action action) {
        super(id);
        this.trigger = trigger;
        this.actions = new ArrayList<Action>();
        actions.add(action);
        this.name = name;
        setTableName(TABLE_NAME);
        setIdColumn(ID_COLUMN);
    }

    public void addAction(Action action){
        actions.add(action);
    }

    public void removeAction(Action action){
        actions.remove(action); //todo probably requires overriding equals in action class
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Action> getActions(){
        return actions;
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
