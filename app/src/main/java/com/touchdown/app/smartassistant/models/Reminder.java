package com.touchdown.app.smartassistant.models;

/**
 * Created by Pete on 13.8.2014.
 */
public class Reminder implements Comparable{

    private long id;
    private String content;
    private boolean alarm_on;

    private ReminderLocation reminderLocation;

    public Reminder(long id, String content, ReminderLocation reminderLocation){
        this.content = content;
        this.id = id;       //remember that id may be -1 and location may be null
        this.reminderLocation = reminderLocation;
    }

    public boolean isOn(){
        return alarm_on;
    }

    public void setOn(boolean alarm_on){
        this.alarm_on = alarm_on;
    }

    public void turnOn(){
        this.alarm_on = true;
    }

    public void turnOff(){
        this.alarm_on = false;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public ReminderLocation getReminderLocation() {
        return reminderLocation;
    }

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    //todo check somewhere that this is a good way to implement equals
    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Reminder)){
            return false;
        }
        Reminder reminder = (Reminder) obj;

        return id == reminder.getId() && content == reminder.getContent();
    }

    @Override
    public int hashCode() {
        int constant = 7;
        int hash = content.hashCode() + constant;
        return hash;
    }


    @Override
    public int compareTo(Object another) {
        Reminder reminder = (Reminder) another;
        if(id < reminder.getId()){
            return -1;
        }else if(id > reminder.getId()){
            return 1;
        }
        return 0;
    }
}
