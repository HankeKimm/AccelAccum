package edu.esu.accelaccum;

import android.location.Location;

import java.util.List;

/**
 * Created by hanke.kimm on 12/30/16.
 */
public class LocationBundle {
    private Location location;
    private float[] accelerometerDataValues;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public float[] getAccelerometerData() {
        return accelerometerDataValues;
    }

    public void setAccelerometerData(float[] accelerometerDataValues) {
        this.accelerometerDataValues = accelerometerDataValues;
    }
}
