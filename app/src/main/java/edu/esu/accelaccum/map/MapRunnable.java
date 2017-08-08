package edu.esu.accelaccum.map;


import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import edu.esu.accelaccum.firebase.FirebaseUtil;
import edu.esu.accelaccum.model.LocationBundle;
import edu.esu.accelaccum.model.MapPoint;

/**
 * Created by hanke.kimm on 3/22/17.
 */

public class MapRunnable implements Runnable  {

    private LocationBundle[] locationBundles;
    //private Activity mapActivity;
    private GoogleMap map;
    private DatabaseReference dbReference;
    private PolylineOptions mapPolyLineOptions;
    private MapPoint previousMapPoint;

    private int dataPointCount;

    private final float x_max_value = 2.5974f;
    private final float y_max_value = 3.1578f;

    private final int CALCULATE_MAXIMUM = 0;
    private final int CALCULATE_MINIMUM = 1;
    private final int CALCULATE_AVERAGE = 2;

    private final String locationDbName = "locations";

    public MapRunnable(GoogleMap map) {
        this.map = map;
        this.mapPolyLineOptions = new PolylineOptions().width(20).color(Color.BLUE).geodesic(false);
        this.dbReference = FirebaseUtil.getFirebaseDatabaseReference().child(locationDbName);
        this.previousMapPoint = null;
        this.dataPointCount = 0;
    }

    public void run() {
        if(locationBundles == null) {
            throw new NullPointerException("locationBundle Array is empty. Set Array before executing runnable");
        }
        MapPoint mapPoint = new MapPoint();
        mapPoint.setLatLng(accelAccumProcess());
        mapPoint.setTimeStamp(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Timestamp(System.currentTimeMillis())));
        mapPoint.setPointCount(++dataPointCount);
        if(dataPointCount % 12 == 0) {
            //calculate the lat and long distance and show a different color on the map.
            
        }
        saveMapPoint(mapPoint);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapPoint.getLatLng(), 20.0f));
        map.addPolyline(mapPolyLineOptions.add((previousMapPoint == null) ? mapPoint.getLatLng() : previousMapPoint.getLatLng(), mapPoint.getLatLng()));
        previousMapPoint = mapPoint;
    }

    public void setLocationArray(LocationBundle[] locationBundles) {
        this.locationBundles = locationBundles;
    }

    private void saveMapPoint(MapPoint mapPoint) {
        dbReference.push().setValue(mapPoint);
    }

    private LatLng accelAccumProcess() {
        double updatedLatitude;
        double updatedLongitude;
        float x_value_total = 0;
        float y_value_total = 0;

        for(int bundleIndex = 0; bundleIndex < locationBundles.length; bundleIndex++) {
            x_value_total += locationBundles[bundleIndex].getAccelerometerData()[1];
            y_value_total += locationBundles[bundleIndex].getAccelerometerData()[2];

        }
        float x_value_avg = x_value_total / 5;
        float y_value_avg = y_value_total / 5;

        if(x_value_avg >= x_max_value) {
            updatedLongitude = calculateUpdatedLongitude(locationBundles, CALCULATE_MAXIMUM);
        }
        else if(x_value_avg < x_max_value) {
            updatedLongitude = calculateUpdatedLongitude(locationBundles, CALCULATE_MINIMUM);
        }
        else {
            updatedLongitude = calculateUpdatedLongitude(locationBundles, CALCULATE_AVERAGE);
        }

        if(y_value_avg >= y_max_value) {
            updatedLatitude = calculateUpdatedLatitude(locationBundles, CALCULATE_MAXIMUM);
        }
        else if(y_value_avg < y_max_value) {
            updatedLatitude = calculateUpdatedLatitude(locationBundles, CALCULATE_MINIMUM);
        }
        else {
            updatedLatitude = calculateUpdatedLatitude(locationBundles, CALCULATE_AVERAGE);
        }

        return new LatLng(updatedLatitude, updatedLongitude);
    }

    private double calculateUpdatedLongitude(LocationBundle[] locationBundleArray, int calculationType) {
        double comparedLongitudeValue = locationBundleArray[0].getLatLng().longitude;
        if(calculationType == CALCULATE_MAXIMUM) {
            for (int bundleIndex = 1; bundleIndex < locationBundleArray.length; bundleIndex++) {
                double currentLongitudeValue = locationBundleArray[bundleIndex].getLatLng().longitude;
                if (currentLongitudeValue > comparedLongitudeValue) {
                    comparedLongitudeValue = currentLongitudeValue;
                }
            }
        }
        else if(calculationType == CALCULATE_MINIMUM) {
            for (int bundleIndex = 1; bundleIndex < locationBundleArray.length; bundleIndex++) {
                double currentLongitudeValue = locationBundleArray[bundleIndex].getLatLng().longitude;
                if (currentLongitudeValue < comparedLongitudeValue) {
                    comparedLongitudeValue = currentLongitudeValue;
                }
            }
        }
        else if(calculationType == CALCULATE_AVERAGE) {
            for (int bundleIndex = 1; bundleIndex < locationBundleArray.length; bundleIndex++) {
                comparedLongitudeValue += locationBundleArray[bundleIndex].getLatLng().longitude;
            }
            comparedLongitudeValue = comparedLongitudeValue / locationBundleArray.length;
        }
        return comparedLongitudeValue;
    }

    private double calculateUpdatedLatitude(LocationBundle[] locationBundleArray, int calculationType) {
        double comparedLatitudeValue = locationBundleArray[0].getLatLng().latitude;
        if(calculationType == CALCULATE_MAXIMUM) {
            for (int bundleIndex = 1; bundleIndex < locationBundleArray.length; bundleIndex++) {
                double currentLatitudeValue = locationBundleArray[bundleIndex].getLatLng().latitude;
                if (currentLatitudeValue > comparedLatitudeValue) {
                    comparedLatitudeValue = currentLatitudeValue;
                }
            }
        }
        else if(calculationType == CALCULATE_MINIMUM) {
            for (int bundleIndex = 1; bundleIndex < locationBundleArray.length; bundleIndex++) {
                double currentLatitudeValue = locationBundleArray[bundleIndex].getLatLng().latitude;
                if (currentLatitudeValue < comparedLatitudeValue) {
                    comparedLatitudeValue = currentLatitudeValue;
                }
            }
        }
        else if(calculationType == CALCULATE_AVERAGE) {
            for (int bundleIndex = 1; bundleIndex < locationBundleArray.length; bundleIndex++) {
                comparedLatitudeValue += locationBundleArray[bundleIndex].getLatLng().latitude;
            }
            comparedLatitudeValue = comparedLatitudeValue / locationBundleArray.length;
        }
        return comparedLatitudeValue;
    }
}
