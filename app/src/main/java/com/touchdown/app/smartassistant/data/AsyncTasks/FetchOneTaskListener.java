package com.touchdown.app.smartassistant.data.asyncTasks;

import com.touchdown.app.smartassistant.models.Task;

/**
 * Created by Pete on 27.8.2014.
 */
public interface FetchOneTaskListener {

    public void deliverTask(Task task);
}
