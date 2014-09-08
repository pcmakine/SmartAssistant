package com.touchdown.app.smartassistant.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import com.touchdown.app.smartassistant.models.ActionType;
import com.touchdown.app.smartassistant.models.RingerVolume;
import com.touchdown.app.smartassistant.services.Common;
import com.touchdown.app.smartassistant.data.DbContract.RingerVolumeEntry;

/**
 * Created by Pete on 1.9.2014.
 */
public class RingerVolumeDao extends Dao<RingerVolume> {

    public RingerVolumeDao(SQLiteOpenHelper dbHelper) {
        super(dbHelper);
    }

    @Override
    protected RingerVolume buildObject(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(RingerVolumeEntry._ID));
        return buildObject(cursor, id);
    }

    public RingerVolume buildObject(Cursor cursor, long id) {
        long actionCollectionId = cursor.getLong(cursor.getColumnIndex(RingerVolumeEntry.COLUMN_NAME_TASK_ID));

        int type = cursor.getInt(cursor.getColumnIndex(RingerVolumeEntry.COLUMN_NAME_TYPE));
        boolean isOn = Common.intAsBoolean(cursor.getInt(cursor.getColumnIndex(RingerVolumeEntry.COLUMN_NAME_ON)));

        RingerVolume rVolume = new RingerVolume(id, ActionType.RINGERVOLUME, isOn, actionCollectionId);

        int volume = cursor.getInt(cursor.getColumnIndex(RingerVolumeEntry.COLUMN_NAME_VOLUME));

        rVolume.setVolume(volume);

        return rVolume;
    }
}
