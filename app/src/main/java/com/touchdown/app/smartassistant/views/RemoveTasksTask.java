package com.touchdown.app.smartassistant.views;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.touchdown.app.smartassistant.ApplicationContextProvider;
import com.touchdown.app.smartassistant.newdb.TaskManager;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Pete on 26.8.2014.
 */
public class RemoveTasksTask extends AsyncTask<List<Long>, Void, Boolean> {

    public static final String LOG_TAG = RemoveTasksTask.class.getSimpleName();

    private WeakReference<RemoveTasksListener> weakActivityReference;


    public RemoveTasksTask(RemoveTasksListener listener){
        super();
        this.weakActivityReference = new WeakReference<RemoveTasksListener>(listener);
    }

    @Override
    protected Boolean doInBackground(List<Long>... params) {
        boolean success = true;
        for(int i = 0; i < params.length; i++){
            if(TaskManager.getInstance(ApplicationContextProvider.getAppContext()).removeTask(params[0].get(i)) != 1){
                success = false;
            }
        }
        return success;
    }

    /**
     * A method that's called once doInBackground() completes. Turn
     * off the indeterminate activity indicator and set
     * the text of the UI element that shows the address. If the
     * lookup failed, display the error message.
     */
    @Override
    protected void onPostExecute(Boolean success) {
        RemoveTasksListener currentActivity = weakActivityReference.get();

        if(currentActivity != null){
            currentActivity.removeSuccessful(success);
        }
    }
}
