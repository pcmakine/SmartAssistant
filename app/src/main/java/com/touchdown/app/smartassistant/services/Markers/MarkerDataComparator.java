package com.touchdown.app.smartassistant.services.Markers;

import com.google.android.gms.maps.model.Marker;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by Pete on 15.8.2014.
 */
public class MarkerDataComparator implements Comparator<Marker> {

    Map<Marker, MarkerData> map;

    public MarkerDataComparator(Map<Marker, MarkerData> map) {
        this.map = map;
    }

    public int compare(Marker keyA, Marker keyB) {

        Comparable valueA = map.get(keyA);
        Comparable valueB = map.get(keyB);

        return valueA.compareTo(valueB);

    }
}
