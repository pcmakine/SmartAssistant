package com.touchdown.app.smartassistant.views;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

/**
 * Created by Pete on 26.8.2014.
 */
public class UpdateTaskTask extends AsyncTask<Long, Void, Boolean> {
    public static final String LOG_TAG = UpdateTaskTask.class.getSimpleName();

    private WeakReference<FetchAllDataListener> weakActivityReference;
    private long fetchingStarted;
    private boolean showProgressIndicator;


    public UpdateTaskTask(FetchAllDataListener listener, boolean showProgressIndicator){
        super();
        this.weakActivityReference = new WeakReference<FetchAllDataListener>(listener);
        this.showProgressIndicator = showProgressIndicator;
    }

    @Override
    protected Boolean doInBackground(Long... params) {
        long id = params[0];

        return null;
    }
}

