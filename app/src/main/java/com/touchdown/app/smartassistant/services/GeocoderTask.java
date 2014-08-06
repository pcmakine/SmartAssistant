package com.touchdown.app.smartassistant.services;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;


import com.touchdown.app.smartassistant.MapActivity;

import java.io.IOException;
import java.util.List;

/*
 Created by Pete on 15.4.2014.
 */

// An AsyncTask class for accessing the GeoCoding Web Service
public class GeocoderTask extends AsyncTask<String, Void, List<Address>> {
    private GoogleMap map;
    private Context ctx;
    LatLng latLng;
    MarkerOptions markerOptions;
    MapActivity activity;

    public GeocoderTask(GoogleMap map, Context ctx, MapActivity activity){
        this.map = map;
        this.ctx = ctx;
        this.activity = activity;
    }

    @Override
    protected List<Address> doInBackground(String... locationName) {
        // Creating an instance of Geocoder class
        Geocoder geocoder = new Geocoder(ctx);
        List<Address> addresses = null;

        try {
            // Getting a maximum of 3 Address that matches the input text
            addresses = geocoder.getFromLocationName(locationName[0], 3);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addresses;
    }

    @Override
    protected void onPostExecute(List<Address> addresses) {
        Marker marker = null;

        if(addresses==null || addresses.size()==0){
            Toast.makeText(ctx.getApplicationContext(), "No Location found", Toast.LENGTH_SHORT).show();
        }else{
            // Clears all the existing markers on the map
            map.clear();

            // Adding Markers on Google Map for each matching address
            for(int i=0;i<addresses.size();i++){

                Address address = (Address) addresses.get(i);

                // Creating an instance of GeoPoint, to display in Google Map
                latLng = new LatLng(address.getLatitude(), address.getLongitude());

                String addressText = String.format("%s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getCountryName());

                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(addressText);

                map.addMarker(markerOptions);

                // Locate the first location
                if(i==0)
                    map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
            activity.setMarker(marker);
        }

    }
}
