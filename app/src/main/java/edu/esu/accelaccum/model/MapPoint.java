package edu.esu.accelaccum.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by hanke.kimm on 3/18/17.
 */

public class MapPoint {

    private LatLng latLng;
    private String timeStamp;
    private int pointCount;

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public int getPointCount() {
        return pointCount;
    }

    public void setPointCount(int pointCount) {
        this.pointCount = pointCount;
    }
}


