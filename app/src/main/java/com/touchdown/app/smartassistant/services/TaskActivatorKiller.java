package com.touchdown.app.smartassistant.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.touchdown.app.smartassistant.models.Task;

import java.util.List;

public class TaskActivatorKiller extends IntentService {


    public TaskActivatorKiller() {
        super("TaskActivatorKiller");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        List<Task> tasks = TaskManager.getInstance(this).getAllTasks();

        boolean noPendingTasks = true;

        for(Task task: tasks){
            if(task.getLocation().isPending()){
                noPendingTasks = false;
                break;
            }
        }
        if(noPendingTasks){
            stopService(new Intent(this, TaskActivator.class));
        }
    }
}
