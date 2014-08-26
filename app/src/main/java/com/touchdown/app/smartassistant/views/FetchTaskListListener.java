package com.touchdown.app.smartassistant.views;

import android.widget.LinearLayout;

import com.touchdown.app.smartassistant.newdb.Task;

import java.util.List;

/**
 * Created by Pete on 26.8.2014.
 */
public interface FetchTaskListListener {

    public void updateTasks(List<Task> taskList);

    public LinearLayout getOnProgressIndicator();
}
