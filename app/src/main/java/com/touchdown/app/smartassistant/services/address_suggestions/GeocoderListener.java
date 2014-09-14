package com.touchdown.app.smartassistant.services.address_suggestions;

import android.location.Address;

import com.google.android.gms.maps.model.Marker;

import java.util.List;

/**
 * Created by Pete on 29.8.2014.
 */
public interface GeocoderListener {

    public void updateAddresses(List addresses);
}
