package com.touchdown.app.smartassistant.newdb;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Pete on 18.8.2014.
 */
public class TriggerLocation extends Trigger {

    public static final int DEFAULT_RADIUS = 100;
    public static final int TRIGGER_TYPE = 0;

    private LatLng latLng;
    private int radius;     //meters
    private long parentId;

    public TriggerLocation(long id, LatLng loc, int radius, long parentId) {
        super(id, 0);
        this.latLng = loc;
        this.parentId = parentId;

        if(radius == 0){
            this.radius = DEFAULT_RADIUS;
        }else{
            this.radius = radius;
        }
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
    
    public void setParentId(long id){
        this.parentId = id;
    }

    public long getParentId() {
        return parentId;
    }
}
