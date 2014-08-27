package com.touchdown.app.smartassistant.services;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.touchdown.app.smartassistant.R;
import com.touchdown.app.smartassistant.views.DetailsActivity;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Pete on 12.8.2014.
 */

//todo: once the address is found save it in the database location, so that it does not need to be fetched every time a reminder is edited
//http://developer.android.com/training/location/display-address.html
public class GetAddressTask extends AsyncTask <LatLng, Void, String> {
    public static final String LOG_TAG = GetAddressTask.class.getSimpleName();
    public static long TASK_EXPIRATION_SECS = 5;

    private WeakReference<DetailsActivity> weakActivityReference;

    public GetAddressTask(DetailsActivity activity) {
        super();
        this.weakActivityReference = new WeakReference<DetailsActivity>(activity);
    }

    /**
     * Get a Geocoder instance, get the latitude and longitude
     * look up the address, and return it
     *
     * @params params One or more LatLng objects
     * @return A string containing the address of the current
     * location, or an empty string if no address can be found,
     * or an error message
     */
    @Override
    protected String doInBackground(LatLng... params) {
/*        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        DetailsActivity currentActivity = weakActivityReference.get();
        if(currentActivity != null && !isCancelled() ){
            Geocoder geocoder =
                    new Geocoder(currentActivity, Locale.getDefault());

            // Get the current location from the input parameter list
            LatLng loc = params[0];
            // Create a list to contain the result address
            List<Address> addresses = null;
            try {
                /*
                 * Return 1 address.
                 */
                addresses = geocoder.getFromLocation(loc.latitude,
                        loc.longitude, 1);
            } catch (IOException e1) {
                Log.e(LOG_TAG,
                        "IO Exception in getFromLocation()");
                e1.printStackTrace();
                return ("IO Exception trying to get address");
            } catch (IllegalArgumentException e2) {
                // Error message to post in the log
                String errorString = "Illegal arguments " +
                        Double.toString(loc.latitude) +
                        " , " +
                        Double.toString(loc.longitude) +
                        " passed to address service";
                Log.e(LOG_TAG, errorString);
                e2.printStackTrace();
                return errorString;
            }
            // If the reverse geocode returned an address
            if (addresses != null && addresses.size() > 0) {
                // Get the first address
                Address address = addresses.get(0);
                String streetAdress = address.getMaxAddressLineIndex() > 0 ?
                        address.getAddressLine(0) + ", " : "";
                String city = address.getLocality() == null ? "": address.getLocality() + ", ";
                String country = address.getCountryName() == null ? "": address.getCountryName();
                StringBuilder sb = new StringBuilder();
                sb.append(streetAdress);
                sb.append(city);
                sb.append(country);
                String addressText = sb.toString();

                /*
                 * Format the first line of address (if available),
                 * city, and country name.
                 */
 /*               String addressText = String.format(
                        "%s, %s, %s",
                        // If there's a street address, add it
                        address.getMaxAddressLineIndex() > 0 ?
                                address.getAddressLine(0) : "",
                        // Locality is usually a city
                        address.getLocality(),
                        // The country of the address
                        address.getCountryName());*/
                // Return the text
                return addressText;
            } else {
                return ApplicationContextProvider.getAppContext().getResources().getString(R.string.error_address_could_not_be_found);
            }
        }
        return ApplicationContextProvider.getAppContext().getResources().getString(R.string.error_address_could_not_be_found);
    }
    /**
     * A method that's called once doInBackground() completes. Turn
     * off the indeterminate activity indicator and set
     * the text of the UI element that shows the address. If the
     * lookup failed, display the error message.
     */
    @Override
    protected void onPostExecute(String address) {
        DetailsActivity currentActivity = weakActivityReference.get();
        Log.d(LOG_TAG, Calendar.getInstance().getTime() + "");
        if(currentActivity != null){
           // currentActivity.deliverAddress(address);
        }
    }

}

