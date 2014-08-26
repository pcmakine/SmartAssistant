package com.touchdown.app.smartassistant.views;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.touchdown.app.smartassistant.newdb.TaskManager;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Pete on 26.8.2014.
 */
public class FetchAllDataTask extends AsyncTask<Context, Void, Cursor> {
    public static final String LOG_TAG = FetchAllDataTask.class.getSimpleName();

    private WeakReference<FetchAllDataListener> weakActivityReference;
    private long fetchingStarted;
    private boolean showProgressIndicator;


    public FetchAllDataTask(FetchAllDataListener listener, boolean showProgressIndicator){
        super();
        this.weakActivityReference = new WeakReference<FetchAllDataListener>(listener);
        this.showProgressIndicator = showProgressIndicator;
    }

    @Override
    protected void onPreExecute() {
        changeProgressIndicatorVisibility(View.VISIBLE);
    }

    private void changeProgressIndicatorVisibility(int visibility){
        if(showProgressIndicator){
            FetchAllDataListener listener = weakActivityReference.get();
            if(listener != null){
                LinearLayout progressIndicator = listener.getOnProgressIndicator();
                progressIndicator.setVisibility(visibility);
            }
        }
    }

    @Override
    protected Cursor doInBackground(Context... params) {
        fetchingStarted = System.currentTimeMillis();
        Log.d(LOG_TAG, "Data fetching started at " + getCurrentTimeStamp());
/*
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        Context context = params[0];
        TaskManager taskManager = TaskManager.getInstance(context);

        Cursor cursor =  taskManager.getAllTaskData();
        return cursor;
    }

    private String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    /**
     * A method that's called once doInBackground() completes. Turn
     * off the indeterminate activity indicator and set
     * the text of the UI element that shows the address. If the
     * lookup failed, display the error message.
     */
    @Override
    protected void onPostExecute(Cursor cursor) {
        long fetchingEnded = System.currentTimeMillis();

        Log.d(LOG_TAG, "Data fetching took " + (fetchingEnded - fetchingStarted) + " ms");

        Log.d(LOG_TAG, "Data fetching ended at " + getCurrentTimeStamp());
        FetchAllDataListener currentActivity = weakActivityReference.get();
        Log.d(LOG_TAG, Calendar.getInstance().getTime() + "");
        if(currentActivity != null){
            currentActivity.updateData(cursor);
            changeProgressIndicatorVisibility(View.GONE);
        }
    }
}
