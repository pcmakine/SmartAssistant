package com.touchdown.app.smartassistant.models;

/**
 * Created by Pete on 13.8.2014.
 */
public class Reminder {

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


}
