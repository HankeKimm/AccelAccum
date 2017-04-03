package edu.esu.accelaccum.model;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by hanke.kimm on 12/30/16.
 */
public class LocationBundle {
    private LatLng latLng;
    private float[] accelerometerDataValues;

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public float[] getAccelerometerData() {
        return accelerometerDataValues;
    }

    public void setAccelerometerData(float[] accelerometerDataValues) {
        this.accelerometerDataValues = accelerometerDataValues;
    }
}
