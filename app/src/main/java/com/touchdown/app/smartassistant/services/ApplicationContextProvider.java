package com.touchdown.app.smartassistant.services;

import android.app.Application;
import android.content.Context;

/**
 * Created by Pete on 14.8.2014.
 */
public class ApplicationContextProvider extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();
        ApplicationContextProvider.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return ApplicationContextProvider.context;
    }
}
