package com.touchdown.app.smartassistant.services;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Pete on 15.8.2014.
 */
public class MyLocationProvider {
    public static final String LOG_TAG = MyLocationProvider.class.getSimpleName();

    private LocationManager locManager;

    public MyLocationProvider(Context ctx){
        locManager = (LocationManager) ctx.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    }

    public Location getCurrentLocation(){
        int minTime = 0;
        float bestAccuracy = Float.MAX_VALUE;
        Location bestResult = null;
        long bestTime = Long.MAX_VALUE;

        List<String> matchingProviders = locManager.getAllProviders();
        String prov = matchingProviders.get(0);
        Location loc = locManager.getLastKnownLocation(prov);

        if(loc != null){
            bestAccuracy = loc.getAccuracy();
            bestResult = loc;
            bestTime = loc.getTime();
        }

        for (String provider: matchingProviders) {
            Location location = locManager.getLastKnownLocation(provider);

            if (location != null) {
                float accuracy = location.getAccuracy();
                long time = location.getTime();

                if ((time > minTime && accuracy < bestAccuracy)) {
                    bestResult = location;
                    bestAccuracy = accuracy;
                    bestTime = time;
                }
                else if (time < minTime &&
                        bestAccuracy == Float.MAX_VALUE && time > bestTime){
                    bestResult = location;
                    bestTime = time;
                }
            }
        }
        return bestResult;
    }

    public boolean isUserInLocation(LatLng latLng, int radiusInMeters){
        return isLocationInArea(getCurrentLocation(), latLng, radiusInMeters);
    }

    public boolean isLocationInArea(Location location, LatLng areaCenter, int radiusInMeters){
        if(location != null){
            double locationLat = location.getLatitude();
            double locationLong = location.getLongitude();
            float accuracy = location.getAccuracy();

            float[] distance = new float[1];
            Location.distanceBetween(locationLat, locationLong, areaCenter.latitude, areaCenter.longitude, distance);

            Log.d(LOG_TAG, "Accuracy: " + accuracy);

            Log.d(LOG_TAG, "Distance between user position and the task center: " +
                    distance[0] + ", radius: " + radiusInMeters);

            return distance[0] < radiusInMeters + accuracy;
        }
        return false;
    }
}
