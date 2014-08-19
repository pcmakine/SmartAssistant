package com.touchdown.app.smartassistant.newdb;

import com.touchdown.app.smartassistant.data.Data;

/**
 * Created by Pete on 18.8.2014.
 */
public class Trigger extends Data {
    private int type;

    public Trigger(long id, int type) {
        super(id);
        this.type = type;
    }

    public int getType(){
        return type;
    }


}
