package com.touchdown.app.smartassistant.models;

import android.content.ContentValues;

import com.touchdown.app.smartassistant.services.Util;
import com.touchdown.app.smartassistant.data.DbContract.RingerVolumeEntry;

/**
 * Created by Pete on 1.9.2014.
 */
public class RingerVolume extends Action {
    private static final String TABLE_NAME = RingerVolumeEntry.TABLE_NAME;
    private static final String ID_COLUMN = RingerVolumeEntry._ID;

    private static final int MIN_VOLUME = 0;
    private static final int DEFAULT_VOLUME = 5;
    private static final int MAX_VOLUME = 10;
    private int volume;

    public RingerVolume(long id, ActionType type, boolean isOn, long actionCollectionId) {
        super(id, type, isOn, actionCollectionId);
        this.volume = DEFAULT_VOLUME;
    }

    public static RingerVolume createDefault(){
        return new RingerVolume(-1, ActionType.RINGERVOLUME, false, -1);
    }

    public void setVolume(int volume){
        if(volume <= MAX_VOLUME && volume >= MIN_VOLUME){
            this.volume = volume;
        }
    }

    @Override
    public void execute() {

    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getIdColumn() {
        return ID_COLUMN;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues vals = new ContentValues();
        vals.put(RingerVolumeEntry.COLUMN_NAME_TASK_ID, this.getActionCollectionId());
        int onInteger = Util.booleanAsInt(isOn());
        vals.put(RingerVolumeEntry.COLUMN_NAME_ON, onInteger);
        vals.put(RingerVolumeEntry.COLUMN_NAME_TYPE, this.getType().value);
        vals.put(RingerVolumeEntry.COLUMN_NAME_VOLUME, volume);

        return vals;
    }
}
