package edu.esu.accelaccum.model;

import android.location.Location;

import java.util.List;

/**
 * Created by hanke.kimm on 12/30/16.
 */
public class LocationBundle {
    private Location location;
    private float[] accelerometerDataValues;
    private String timeStamp;

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

    public String getTmestamp() {
        return timeStamp;
    }

    public void setTimestamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}