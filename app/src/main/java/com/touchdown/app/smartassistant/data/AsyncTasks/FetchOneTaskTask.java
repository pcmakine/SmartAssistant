package com.touchdown.app.smartassistant.data.AsyncTasks;

import android.os.AsyncTask;

import com.touchdown.app.smartassistant.services.ApplicationContextProvider;
import com.touchdown.app.smartassistant.models.Task;
import com.touchdown.app.smartassistant.services.TaskManager;

import java.lang.ref.WeakReference;

/**
 * Created by Pete on 27.8.2014.
 */
public class FetchOneTaskTask extends AsyncTask<Long, Void, Task> {

    public static final String LOG_TAG = FetchAllDataTask.class.getSimpleName();

    private WeakReference<FetchOneTaskListener> weakActivityReference;


    public FetchOneTaskTask(FetchOneTaskListener listener){
        super();
        this.weakActivityReference = new WeakReference<FetchOneTaskListener>(listener);
    }


    @Override
    protected Task doInBackground(Long... params) {
        Long id = params[0];
        TaskManager tManager = TaskManager.getInstance(ApplicationContextProvider.getAppContext());
        Task task = tManager.findTaskById(id);
        return task;
    }

    @Override
    protected void onPostExecute(Task task) {

        FetchOneTaskListener currentActivity = weakActivityReference.get();
        if(currentActivity != null){
            currentActivity.deliverTask(task);
        }
    }
}
