package com.touchdown.app.smartassistant.data.asyncTasks;

import com.touchdown.app.smartassistant.models.Task;

import java.util.List;

/**
 * Created by Pete on 8.9.2014.
 */
public interface FetchAllTasksListener {

    public void update(List<Task> taskList);
}
