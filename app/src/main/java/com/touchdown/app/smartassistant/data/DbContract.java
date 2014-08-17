package com.touchdown.app.smartassistant.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Pete on 2.8.2014.
 */
public class DbContract {

    public static final class ReminderEntry implements BaseColumns {
        public static final String TABLE_NAME = "reminder";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_ON = "alarm_on";
    }

    public static final class LocationEntry implements BaseColumns {
        public static final String TABLE_NAME = "location";
        public static final String COLUMN_NAME_REMINDER_ID = "reminderId";
        public static final String COLUMN_NAME_LAT = "lat";
        public static final String COLUMN_NAME_LONG = "lng";
        public static final String COLUMN_NAME_RADIUS = "radius";
    }

    public static final class TimeEntry implements BaseColumns {
        public static final String TABLE_NAME = "time";
        public static final String COLUMN_NAME_REMINDER_ID = "reminderId";
        public static final String COLUMN_NAME_DATE = "date";
    }
}
