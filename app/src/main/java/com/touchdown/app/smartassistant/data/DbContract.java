package com.touchdown.app.smartassistant.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Pete on 2.8.2014.
 */
public class DbContract {

    public static final class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "task";
        public static final String COLUMN_NAME_TASK_NAME = "taskName";
    }

    public static final class AlarmEntry implements BaseColumns {
        public static final String TABLE_NAME = "reminder";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_ON = "alarm_on";
        public static final String COLUMN_NAME_TYPE = "actionType";
        public static final String COLUMN_NAME_NOTIFICATION_ENABLED = "notification";
        public static final String COLUMN_NAME_FULLSCREEN_ENABLED = "fullscreen";
        public static final String COLUMN_NAME_TASK_ID = "reminderTaskId";
    }


    public static final class RingerVolumeEntry implements BaseColumns {
        public static final String TABLE_NAME = "ringerVolume";
        public static final String COLUMN_NAME_VOLUME = "volume";
        public static final String COLUMN_NAME_TASK_ID = "ringerVolumeTaskId";
        public static final String COLUMN_NAME_ON = "alarm_on";
        public static final String COLUMN_NAME_TYPE = "actionType";
    }

    public static final class LocationEntry implements BaseColumns {
        public static final String TABLE_NAME = "location";
        public static final String COLUMN_NAME_LAT = "lat";
        public static final String COLUMN_NAME_LONG = "lng";
        public static final String COLUMN_NAME_RADIUS = "radius";
        public static final String COLUMN_NAME_TRIGGER_ON_ARRIVAL = "triggerOnArrival";
        public static final String COLUMN_NAME_TRIGGER_ON_DEPARTURE = "triggerOnDeparture";
        public static final String COLUMN_NAME_TRIGGER_TYPE = "triggerType";
        public static final String COLUMN_NAME_PENDING = "pending";
        public static final String COLUMN_NAME_TASK_ID = "locationTaskId";
    }

    public static final class TimeEntry implements BaseColumns {
        public static final String TABLE_NAME = "time";
        public static final String COLUMN_NAME_REMINDER_ID = "reminderId";
        public static final String COLUMN_NAME_DATE = "date";
    }
}
