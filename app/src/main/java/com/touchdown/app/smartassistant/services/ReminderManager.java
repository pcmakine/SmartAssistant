package com.touchdown.app.smartassistant.services;

import com.touchdown.app.smartassistant.data.DbHelper;
import com.touchdown.app.smartassistant.models.ReminderDao;

/**
 * Created by Pete on 13.8.2014.
 */
public class ReminderManager {

    private DbHelper dbHelper;

    public ReminderManager(DbHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    public void insert(ReminderDao reminder){
        reminder.insert(dbHelper);
    }


}
