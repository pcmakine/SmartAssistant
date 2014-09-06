package com.touchdown.app.smartassistant.models;

import android.content.ContentValues;
import android.content.Context;
import android.media.AudioManager;
import android.os.Parcel;
import android.os.Parcelable;

import com.touchdown.app.smartassistant.services.ApplicationContextProvider;
import com.touchdown.app.smartassistant.services.Util;
import com.touchdown.app.smartassistant.data.DbContract.RingerVolumeEntry;

/**
 * Created by Pete on 1.9.2014.
 */
public class RingerVolume extends Action implements Parcelable {
    private static final String TABLE_NAME = RingerVolumeEntry.TABLE_NAME;
    private static final String ID_COLUMN = RingerVolumeEntry._ID;
    private static final int NUMBER_OF_BOOLEANS = 1;

    private static final int MIN_VOLUME = 0;
    private static final int DEFAULT_VOLUME = 50;
    private static final int MAX_VOLUME = 100;
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

    public int getVolume(){
        return volume;
    }

    @Override
    public void execute() {
        AudioManager aManager = (AudioManager) ApplicationContextProvider.getAppContext().
                getSystemService(Context.AUDIO_SERVICE);
        int maxVol = aManager.getStreamMaxVolume(AudioManager.STREAM_RING);

        double volMultiplier = (volume/(MAX_VOLUME * 1.0));

        aManager.setStreamVolume(AudioManager.STREAM_RING, (int) volMultiplier*maxVol, AudioManager.FLAG_VIBRATE);

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
        vals.put(RingerVolumeEntry.COLUMN_NAME_TASK_ID, this.getTaskId());
        int onInteger = Util.booleanAsInt(isOn());
        vals.put(RingerVolumeEntry.COLUMN_NAME_ON, onInteger);
        vals.put(RingerVolumeEntry.COLUMN_NAME_TYPE, this.getType().value);
        vals.put(RingerVolumeEntry.COLUMN_NAME_VOLUME, volume);

        return vals;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        //this class
        dest.writeInt(volume);
    }

    public static final Parcelable.Creator<RingerVolume> CREATOR
            = new Parcelable.Creator<RingerVolume>() {
        public RingerVolume createFromParcel(Parcel in) {
            return new RingerVolume(in);
        }

        public RingerVolume[] newArray(int size) {
            return new RingerVolume[size];
        }
    };

    private RingerVolume(Parcel in) {
        //superclass
        setId(in.readLong());
        setTaskId(in.readLong());
        setType(ActionType.getEnum(in.readInt()));

        boolean[] booleans = new boolean[NUMBER_OF_BOOLEANS];
        in.readBooleanArray(booleans);

        setOn(booleans[0]);

        //this class
        volume = in.readInt();

    }
}
