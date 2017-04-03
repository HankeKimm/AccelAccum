package edu.esu.accelaccum.util;

import edu.esu.accelaccum.model.LocationBundle;

/**
 * Created by hanke.kimm on 3/30/17.
 */

public class AccelAccumUtil {
    public static LocationBundle[] copyArray(LocationBundle[] locationBundles) {
        LocationBundle[] newLocationBundleArray = new LocationBundle[locationBundles.length];
        for(int locationBundleIndex = 0; locationBundleIndex < locationBundles.length; locationBundleIndex++) {
            newLocationBundleArray[locationBundleIndex] = locationBundles[locationBundleIndex];
        }
        return newLocationBundleArray;
    }
}
