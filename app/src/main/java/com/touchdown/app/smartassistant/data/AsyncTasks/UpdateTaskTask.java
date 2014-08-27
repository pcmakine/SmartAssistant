package com.touchdown.app.smartassistant.data.AsyncTasks;

import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;

import com.touchdown.app.smartassistant.ApplicationContextProvider;
import com.touchdown.app.smartassistant.models.Task;
import com.touchdown.app.smartassistant.services.TaskManager;

import java.lang.ref.WeakReference;

/**
 * Created by Pete on 26.8.2014.
 */
public class UpdateTaskTask extends AsyncTask<Task, Void, Boolean> {
    public static final String LOG_TAG = UpdateTaskTask.class.getSimpleName();

    private WeakReference<UpdateTaskListener> weakActivityReference;
    private boolean showProgressIndicator;
    private boolean insert;


    public UpdateTaskTask(UpdateTaskListener listener, boolean showProgressIndicator, boolean insert){
        super();
        this.weakActivityReference = new WeakReference<UpdateTaskListener>(listener);
        this.showProgressIndicator = showProgressIndicator;
        this.insert = insert;
    }

    @Override
    protected void onPreExecute() {
        changeProgressIndicatorVisibility(View.VISIBLE);
    }

    private void changeProgressIndicatorVisibility(int visibility){
        if(showProgressIndicator){
            UpdateTaskListener listener = weakActivityReference.get();
            if(listener != null){
                LinearLayout progressIndicator = listener.getOnProgressIndicator();
                progressIndicator.setVisibility(visibility);
            }
        }
    }

    @Override
    protected Boolean doInBackground(Task... params) {
        Task task = params[0];
        TaskManager manager = TaskManager.getInstance(ApplicationContextProvider.getAppContext());

        if(insert){
            long id = manager.insert(task);
            return id != -1;
        }else{
            boolean success = manager.update(task);
            return success;
        }
    }

    /**
     * A method that's called once doInBackground() completes. Turn
     * off the indeterminate activity indicator and set
     * the text of the UI element that shows the address. If the
     * lookup failed, display the error message.
     */
    @Override
    protected void onPostExecute(Boolean success) {

        UpdateTaskListener currentActivity = weakActivityReference.get();
        if(currentActivity != null){
            currentActivity.updateSuccessful(success);
            changeProgressIndicatorVisibility(View.GONE);
        }
    }
}

