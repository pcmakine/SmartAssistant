package com.touchdown.app.smartassistant.data.AsyncTasks;

import android.widget.LinearLayout;

/**
 * Created by Pete on 27.8.2014.
 */
public interface UpdateTaskListener {

    public void updateSuccessful(boolean success);

    public LinearLayout getOnProgressIndicator();
}
