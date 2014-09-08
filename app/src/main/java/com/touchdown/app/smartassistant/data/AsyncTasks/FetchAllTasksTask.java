package com.touchdown.app.smartassistant.data.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.touchdown.app.smartassistant.models.Task;
import com.touchdown.app.smartassistant.services.TaskManager;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Pete on 8.9.2014.
 */
public class FetchAllTasksTask extends AsyncTask<Context, Void, List<Task>> {

    public static final String LOG_TAG = FetchAllDataTask.class.getSimpleName();

    private WeakReference<FetchAllTasksListener> weakActivityReference;
    private long fetchingStarted;
    private boolean showProgressIndicator;


    public FetchAllTasksTask(FetchAllTasksListener listener){
        super();
        this.weakActivityReference = new WeakReference<FetchAllTasksListener>(listener);
    }

    @Override
    protected List<Task> doInBackground(Context... params) {
        fetchingStarted = System.currentTimeMillis();
        Log.d(LOG_TAG, "Data fetching started at " + getCurrentTimeStamp());

        TaskManager manager = TaskManager.getInstance(params[0]);

        return manager.getAllTasks();
    }

    private String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    @Override
    protected void onPostExecute(List<Task> tasks) {
        long fetchingEnded = System.currentTimeMillis();

        Log.d(LOG_TAG, "Data fetching took " + (fetchingEnded - fetchingStarted) + " ms");

        Log.d(LOG_TAG, "Data fetching ended at " + getCurrentTimeStamp());
        FetchAllTasksListener currentActivity = weakActivityReference.get();
        if(currentActivity != null){
            currentActivity.update(tasks);
        }
    }

}
