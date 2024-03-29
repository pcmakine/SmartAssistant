package com.touchdown.app.smartassistant.services.address_suggestions;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.touchdown.app.smartassistant.services.ApplicationContextProvider;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;


/*
 Created by Pete on 15.4.2014.
 */

// An AsyncTask class for accessing the GeoCoding Web Service
public class GeocoderTask extends AsyncTask<String, Void, List<Address>> {
    private static final int MAX_NUMBER_OF_ADDRESSES_TO_RETURN = 10;

    private GoogleMap map;
    LatLng latLng;
    MarkerOptions markerOptions;
    GeocoderListener activity;
    private boolean userClickedSearch;
    private WeakReference<GeocoderListener> weakActifityReference;


    public GeocoderTask(GeocoderListener activity, boolean userClickedSearch){
        this.map = map;
        this.weakActifityReference = new WeakReference<GeocoderListener>(activity);
    }

    @Override
    protected List<Address> doInBackground(String... locationName) {
        // Creating an instance of Geocoder class


        Geocoder geocoder = new Geocoder(ApplicationContextProvider.getAppContext());
        
        List<Address> addresses = null;

        try {
            // Getting a maximum of 3 Address that matches the input text
            addresses = geocoder.getFromLocationName(locationName[0], 10);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addresses;
    }

    @Override
    protected void onPostExecute(List<Address> addresses) {
        Marker marker = null;

        if((addresses==null || addresses.size()==0) && userClickedSearch){
            Toast.makeText(ApplicationContextProvider.getAppContext(), "No Location found", Toast.LENGTH_SHORT).show();
        }else{

            GeocoderListener listener = weakActifityReference.get();
            if(listener != null){
                listener.updateAddresses(addresses);
            }

/*            // Adding Markers on Google Map for each matching address
            for(int i=0;i<1;i++){   //todo add all found ones or the closest ones etc

                Address address = (Address) addresses.get(i);

                // Creating an instance of GeoPoint, to display in Google Map
                latLng = new LatLng(address.getLatitude(), address.getLongitude());

                String addressText = String.format("%s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getCountryName());

                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(addressText);

                marker = map.addMarker(markerOptions);

                // Locate the first location
                if(i==0)
                    map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
            activity.setMarker(marker);*/
        }

    }
}
