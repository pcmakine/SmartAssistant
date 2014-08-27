package com.touchdown.app.smartassistant.data.AsyncTasks;

import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;

import com.touchdown.app.smartassistant.ApplicationContextProvider;
import com.touchdown.app.smartassistant.models.Task;
import com.touchdown.app.smartassistant.services.TaskManager;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Pete on 26.8.2014.
 */
public class FetchAllLocationTasksTask extends AsyncTask<Void, Void, List<Task>> {

    public static final String LOG_TAG = FetchAllDataTask.class.getSimpleName();

    private WeakReference<FetchTaskListListener> weakActivityReference;
    private boolean showProgressIndicator;


    public FetchAllLocationTasksTask(FetchTaskListListener listener, boolean showProgressIndicator){
        super();
        this.weakActivityReference = new WeakReference(listener);
        this.showProgressIndicator = showProgressIndicator;
    }


    private void changeProgressIndicatorVisibility(int visibility){
        if(showProgressIndicator){
            FetchTaskListListener listener = weakActivityReference.get();
            if(listener != null){
                LinearLayout progressIndicator = listener.getOnProgressIndicator();
                if(progressIndicator != null){
                    progressIndicator.setVisibility(visibility);
                }
            }
        }
    }

    @Override
    protected List doInBackground(Void... params) {
        TaskManager tManager = TaskManager.getInstance(ApplicationContextProvider.getAppContext());
        List<Task> tasks = tManager.getAllTasksWithLocationTrigger();
        return tasks;
    }

    @Override
    protected void onPreExecute() {
        changeProgressIndicatorVisibility(View.VISIBLE);
    }

    /**
     * A method that's called once doInBackground() completes. Turn
     * off the indeterminate activity indicator and set
     * the text of the UI element that shows the address. If the
     * lookup failed, display the error message.
     */
    @Override
    protected void onPostExecute(List<Task> tasks) {

        FetchTaskListListener currentActivity = weakActivityReference.get();
        if(currentActivity != null){
            currentActivity.updateTasks(tasks);
            changeProgressIndicatorVisibility(View.GONE);
        }
    }
}
