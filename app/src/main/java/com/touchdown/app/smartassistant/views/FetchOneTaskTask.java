package com.touchdown.app.smartassistant.views;

import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;

import com.touchdown.app.smartassistant.ApplicationContextProvider;
import com.touchdown.app.smartassistant.newdb.Task;
import com.touchdown.app.smartassistant.newdb.TaskManager;

import java.lang.ref.WeakReference;
import java.util.List;

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
