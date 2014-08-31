package com.touchdown.app.smartassistant.services;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Pete on 29.8.2014.
 */
public class LocationSpoofer {
    private LocationManager lmanager;
    private static final String TEST_PROVIDER_NAME = LocationManager.NETWORK_PROVIDER;
    private double lat = 60.230039;
    private double longitude = 25.025155;
    private double distToMoveOnButtonClick = 0.005;

    public LocationSpoofer(){
        this.lmanager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
    }

    private Context getContext(){
        Context context = ApplicationContextProvider.getAppContext();
        return context;
    }

    public void enable(){
        lmanager.addTestProvider(TEST_PROVIDER_NAME, false, false, false, false, false, false, false, Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
        lmanager.setTestProviderEnabled(TEST_PROVIDER_NAME, true);
    }

    public void moveLeft(){
        this.longitude = this.longitude - distToMoveOnButtonClick;
        updateLoc();
    }

    public boolean isEnabled(){
        return lmanager.getProvider(TEST_PROVIDER_NAME) != null;
    }

    public void moveRight(){
        this.longitude = this.longitude + distToMoveOnButtonClick;
        updateLoc();
    }

    private void updateLoc()  {
        Location location = new Location(TEST_PROVIDER_NAME);


        Method locationJellyBeanFixMethod = null;
        try {
            locationJellyBeanFixMethod = Location.class.getMethod("makeComplete");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (locationJellyBeanFixMethod != null) {
            try {
                locationJellyBeanFixMethod.invoke(location);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        location.setLatitude(lat);
        location.setLongitude(longitude);
        location.setAccuracy(5);
        lmanager.setTestProviderLocation(TEST_PROVIDER_NAME, location);
    }

    public void disable(){
        lmanager.removeTestProvider(TEST_PROVIDER_NAME);
    }

    public boolean isEnabled(String name){
        return lmanager.getProvider(name) != null;
    }


    public void disable(String name){
        if(isEnabled(name)){
            lmanager.removeTestProvider(name);
        }
    }

}
