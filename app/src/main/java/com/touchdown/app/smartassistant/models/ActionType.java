package com.touchdown.app.smartassistant.models;

/**
 * Created by Pete on 1.9.2014.
 */
public enum ActionType {
    ALARM(0), RINGERVOLUME(1);

    public final int value;

    ActionType(final int value) {
        this.value = value;
    }

    public static ActionType getEnum(int value) {
        for(ActionType e: ActionType.values()) {
            if(e.value == value) {
                return e;
            }
        }
        return null;// not found
    }
}
