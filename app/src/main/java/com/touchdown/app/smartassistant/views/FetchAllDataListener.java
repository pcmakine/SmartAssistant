package com.touchdown.app.smartassistant.views;

import android.database.Cursor;
import android.widget.LinearLayout;

/**
 * Created by Pete on 26.8.2014.
 */
public interface FetchAllDataListener {

    public void updateData(Cursor cursor);

    public LinearLayout getOnProgressIndicator();
}
