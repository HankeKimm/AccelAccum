package edu.esu.accelaccum.module;

import android.app.Activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.content.Context;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.GoogleMap;

import edu.esu.accelaccum.task.AccelAccumProcessTask;
import edu.esu.accelaccum.model.LocationBundle;
import edu.esu.accelaccum.R;

/**
 * Created by hanke.kimm on 12/30/16.
 */
public class LocationModule implements LocationListener {

    private LocationManager locationManager;
    private GoogleMap map;
    private Activity mapActivity;
    private ToggleButton toggleButton;
    private AccelerometerModule accelerometerModule;
    private LocationBundle[] locationBundleArray;

    private final int dataPointBuffer = 5;
    private int dataPointCounter = 0;
    private final int locationTimeInterval = 500;
    private final float locationDistanceInterval = 0f;

    private class AccelerometerModule extends Thread implements SensorEventListener {

        private SensorManager sensorManager;
        private Sensor accelerometerSensor;
        private float[] accelerometerValues;
        private Handler accelerometerHandler;

        private final int accelerometerType = Sensor.TYPE_ACCELEROMETER;

        public AccelerometerModule(Context context) {
            sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            accelerometerSensor = sensorManager.getDefaultSensor(accelerometerType);
        }

        @Override
        public void run() {
            Looper.prepare();
            accelerometerHandler = new Handler();
            sensorManager.registerListener(this, accelerometerSensor, 0, accelerometerHandler);
            Looper.loop();
        }

        private void quit() {
            accelerometerHandler.getLooper().quit();
            sensorManager.unregisterListener(this, accelerometerSensor);
        }

        private float[] getAccelerometerValues() {
            return accelerometerValues;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            accelerometerValues = event.values;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    public LocationModule(Context context, GoogleMap map) {
        this.mapActivity = (Activity) context;
        this.map = map;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public boolean isEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void start() throws SecurityException {
        locationBundleArray = new LocationBundle[dataPointBuffer];
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, locationTimeInterval, locationDistanceInterval, this);
        if (accelerometerModule == null) {
            accelerometerModule = new AccelerometerModule(mapActivity);
        }
        accelerometerModule.start();
    }

    public void stop() throws SecurityException {
        locationManager.removeUpdates(this);
        accelerometerModule.quit();
        locationBundleArray = null;
        accelerometerModule = null;
    }

    @Override
    public void onLocationChanged(Location location) throws SecurityException {
        if(locationBundleArray[dataPointBuffer - 1] == null) {
            LocationBundle locationBundle = new LocationBundle();
            locationBundle.setLocation(location);
            locationBundle.setAccelerometerData(null);
            locationBundleArray[dataPointCounter] = locationBundle;
            locationBundleArray[dataPointCounter].setAccelerometerData(accelerometerModule.getAccelerometerValues());
            dataPointCounter++;
        } else {
            dataPointCounter = 0;
            LocationBundle[] processArray = locationBundleArray.clone();
            stop();
            new AccelAccumProcessTask(this, mapActivity, map).execute(processArray);
            start();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) throws SecurityException {
    }
}
