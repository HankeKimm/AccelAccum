package edu.esu.accelaccum.task;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.CameraUpdateFactory;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.esu.accelaccum.LocationBundle;
import edu.esu.accelaccum.module.LocationModule;

import java.sql.Timestamp;


/**
 * Created by hanke.kimm on 1/31/17.
 */
public class AccelAccumProcessTask extends AsyncTask<LocationBundle, Void, LatLng> {

    private LocationModule locationModule;
    private Activity mapActivity;
    private GoogleMap map;

    private final float x_max_value = 2.5974f;
    private final float y_max_value = 3.1578f;

    private final int CALCULATE_MAXIMUM = 0;
    private final int CALCULATE_MINIMUM = 1;
    private final int CALCULATE_AVERAGE = 2;

    public AccelAccumProcessTask(LocationModule locationModule, Activity mapActivity, GoogleMap map) {
        this.locationModule = locationModule;
        this.mapActivity = mapActivity;
        this.map = map;
    }

    @Override
    protected LatLng doInBackground(LocationBundle... params) {
        return accelAccumProcess(params);
    }

    @Override
    protected void onPostExecute(LatLng updatedLatLng) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(updatedLatLng, 20.0f));
        //map.addMarker(new MarkerOptions().position(updatedLatLng));
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        new Timestamp(System.currentTimeMillis());
        //myRef.child("locations").child(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Timestamp(System.currentTimeMillis()))).setValue(updatedLatLng);
        myRef.child("locations").child(String.valueOf(new Timestamp(System.currentTimeMillis()).getTime())).setValue(updatedLatLng);

        //locationModule.start();
        map.addPolyline((new PolylineOptions()).add(updatedLatLng).width(20).color(Color.BLUE).geodesic(false));

    }

    private LatLng accelAccumProcess(LocationBundle[] locationBundleArray) {
        double updatedLatitude;
        double updatedLongitude;
        float x_value_total = 0;
        float y_value_total = 0;

        for(int bundleIndex = 0; bundleIndex < locationBundleArray.length; bundleIndex++) {
            x_value_total += locationBundleArray[bundleIndex].getAccelerometerData()[1];
            y_value_total += locationBundleArray[bundleIndex].getAccelerometerData()[2];

        }
        float x_value_avg = x_value_total / 5;
        float y_value_avg = y_value_total / 5;

        if(x_value_avg >= x_max_value) {
            updatedLongitude = calculateUpdatedLongitude(locationBundleArray, CALCULATE_MAXIMUM);
        }
        else if(x_value_avg < x_max_value) {
            updatedLongitude = calculateUpdatedLongitude(locationBundleArray, CALCULATE_MINIMUM);
        }
        else {
            updatedLongitude = calculateUpdatedLongitude(locationBundleArray, CALCULATE_AVERAGE);
        }

        if(y_value_avg >= y_max_value) {
            updatedLatitude = calculateUpdatedLatitude(locationBundleArray, CALCULATE_MAXIMUM);
        }
        else if(y_value_avg < y_max_value) {
            updatedLatitude = calculateUpdatedLatitude(locationBundleArray, CALCULATE_MINIMUM);
        }
        else {
            updatedLatitude = calculateUpdatedLatitude(locationBundleArray, CALCULATE_AVERAGE);
        }

        return new LatLng(updatedLatitude, updatedLongitude);
    }

    private double calculateUpdatedLongitude(LocationBundle[] locationBundleArray, int calculationType) {
        double comparedLongitudeValue = locationBundleArray[0].getLocation().getLongitude();
        if(calculationType == CALCULATE_MAXIMUM) {
            for (int bundleIndex = 1; bundleIndex < locationBundleArray.length; bundleIndex++) {
                double currentLongitudeValue = locationBundleArray[bundleIndex].getLocation().getLongitude();
                if (currentLongitudeValue > comparedLongitudeValue) {
                    comparedLongitudeValue = currentLongitudeValue;
                }
            }
        }
        else if(calculationType == CALCULATE_MINIMUM) {
            for (int bundleIndex = 1; bundleIndex < locationBundleArray.length; bundleIndex++) {
                double currentLongitudeValue = locationBundleArray[bundleIndex].getLocation().getLongitude();
                if (currentLongitudeValue < comparedLongitudeValue) {
                    comparedLongitudeValue = currentLongitudeValue;
                }
            }
        }
        else if(calculationType == CALCULATE_AVERAGE) {
            for (int bundleIndex = 1; bundleIndex < locationBundleArray.length; bundleIndex++) {
                comparedLongitudeValue += locationBundleArray[bundleIndex].getLocation().getLongitude();
            }
            comparedLongitudeValue = comparedLongitudeValue / locationBundleArray.length;
        }
            return comparedLongitudeValue;
    }

    private double calculateUpdatedLatitude(LocationBundle[] locationBundleArray, int calculationType) {
        double comparedLatitudeValue = locationBundleArray[0].getLocation().getLatitude();
        if(calculationType == CALCULATE_MAXIMUM) {
            for (int bundleIndex = 1; bundleIndex < locationBundleArray.length; bundleIndex++) {
                double currentLatitudeValue = locationBundleArray[bundleIndex].getLocation().getLatitude();
                if (currentLatitudeValue > comparedLatitudeValue) {
                    comparedLatitudeValue = currentLatitudeValue;
                }
            }
        }
        else if(calculationType == CALCULATE_MINIMUM) {
            for (int bundleIndex = 1; bundleIndex < locationBundleArray.length; bundleIndex++) {
                double currentLatitudeValue = locationBundleArray[bundleIndex].getLocation().getLatitude();
                if (currentLatitudeValue < comparedLatitudeValue) {
                    comparedLatitudeValue = currentLatitudeValue;
                }
            }
        }
        else if(calculationType == CALCULATE_AVERAGE) {
            for (int bundleIndex = 1; bundleIndex < locationBundleArray.length; bundleIndex++) {
                comparedLatitudeValue += locationBundleArray[bundleIndex].getLocation().getLatitude();
            }
            comparedLatitudeValue = comparedLatitudeValue / locationBundleArray.length;
        }
        return comparedLatitudeValue;
    }

}
