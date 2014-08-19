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

    public static final class ActionEntry implements BaseColumns {
        public static final String TABLE_NAME = "action";
        public static final String COLUMN_NAME_ACTION_NAME = "actionName";
        public static final String COLUMN_NAME_ACTION_TYPE = "actionType";
    }

    public static final class ActionTriggerEntry {
        public static final String TABLE_NAME = "action_trigger";
        public static final String COLUMN_NAME_ACTION_ID = "actionId";
        public static final String COLUMN_NAME_TRIGGER_ID = "triggerId";
    }

    public static final class TriggerEntry implements BaseColumns {
        public static final String TABLE_NAME = "trigger";
        public static final String COLUMN_NAME_TRIGGER_TYPE = "triggerType";
    }

    public static final class LocationEntry implements BaseColumns {
        public static final String TABLE_NAME = "location";
        public static final String COLUMN_NAME_LAT = "lat";
        public static final String COLUMN_NAME_LONG = "lng";
        public static final String COLUMN_NAME_RADIUS = "radius";
        public static final String COLUMN_NAME_TRIGGER_TYPE = "triggerType";
        public static final String COLUMN_NAME_PARENT_ID = "parentId";
    }

    public static final class TimeEntry implements BaseColumns {
        public static final String TABLE_NAME = "time";
        public static final String COLUMN_NAME_REMINDER_ID = "reminderId";
        public static final String COLUMN_NAME_DATE = "date";
    }
}
