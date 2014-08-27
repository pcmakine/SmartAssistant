package com.touchdown.app.smartassistant.views;

import com.touchdown.app.smartassistant.newdb.Task;

/**
 * Created by Pete on 27.8.2014.
 */
public interface FetchOneTaskListener {

    public void deliverTask(Task task);
}
