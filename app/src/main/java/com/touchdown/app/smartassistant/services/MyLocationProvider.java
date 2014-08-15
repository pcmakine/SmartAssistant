package com.touchdown.app.smartassistant.services;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import java.util.List;

/**
 * Created by Pete on 15.8.2014.
 */
public class MyLocationProvider {
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
}
