package com.touchdown.app.smartassistant.services;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Pete on 15.8.2014.
 */
public class MapSort {

    public static Map sortByValue(Map unsortedMap){
        Map sortedMap = new TreeMap(new MarkerDataComparator(unsortedMap));
        sortedMap.putAll(unsortedMap);
        return sortedMap;
    }
}
