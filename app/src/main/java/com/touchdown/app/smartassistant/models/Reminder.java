package com.touchdown.app.smartassistant.models;

/**
 * Created by Pete on 13.8.2014.
 */
public class Reminder implements Comparable{

    private long id;
    private String content;
    private LocationDao location;
    private boolean isOn;

    public Reminder(long id, String content, LocationDao location){
        this.content = content;
        this.id = id;       //remember that id may be -1 and location may be null
        this.location = location;
    }

    public boolean isOn(){
        return isOn;
    }

    public void setOn(boolean on){
        this.isOn = on;
    }

    public void turnOn(){
        this.isOn = true;
    }

    public void turnOff(){
        this.isOn = false;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public LocationDao getLocation() {
        return location;
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
