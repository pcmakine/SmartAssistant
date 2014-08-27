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

    public Location getLocation(){
        int minTime = 0;
        List<String> matchingProviders = locManager.getAllProviders();
        String prov = matchingProviders.get(0);
        Location loc = locManager.getLastKnownLocation(prov);
        float bestAccuracy = loc.getAccuracy();
        Location bestResult = loc;
        long bestTime = loc.getTime();

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
        Location userLocation = getLocation();

        float accuracy = userLocation.getAccuracy();

        double userLat = userLocation.getLatitude();
        double userLong = userLocation.getLongitude();

        float[] distance = new float[1];
        Location.distanceBetween(userLat, userLong, latLng.latitude, latLng.longitude, distance);

        Log.d(LOG_TAG, "Accuracy: " + accuracy);

        Log.d(LOG_TAG, "Distance between user position and the task center: " +
                distance[0] + ", radius: " + radiusInMeters);

        if(distance[0] < radiusInMeters + accuracy){
            return true;
        }
        return false;
    }
}
