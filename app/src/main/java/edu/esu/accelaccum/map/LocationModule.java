package edu.esu.accelaccum.map;

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
import android.widget.ToggleButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import edu.esu.accelaccum.model.LocationBundle;
import edu.esu.accelaccum.util.AccelAccumUtil;

/**
 * Created by hanke.kimm on 12/30/16.
 */
//TODO: Turn Location Module to a singleton
public class LocationModule implements LocationListener {

    private LocationManager locationManager;
    private GoogleMap map;
    private Activity mapActivity;
    private AccelerometerModule accelerometerModule;
    private LocationBundle[] locationBundleArray;
    private Handler mapHandler;
    private MapRunnable mapRunnable;

    private final int dataPointBuffer = 5;
    private int dataPointCounter = 0;
    private final int locationTimeInterval = 500;
    private final float locationDistanceInterval = 0.5f;

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
        mapHandler = new Handler();
        mapRunnable = new MapRunnable(map);
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
        //locationBundleArray = null;
        accelerometerModule = null;
    }

    @Override
    public void onLocationChanged(Location location) throws SecurityException {
        if(locationBundleArray[dataPointBuffer - 1] == null) {
            LocationBundle locationBundle = new LocationBundle();
            locationBundle.setLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            float[] valuesArray = new float[] {0.0f, 0.0f, 9.8f};
            float[] sensorArray = accelerometerModule.getAccelerometerValues();
            valuesArray[0] = sensorArray[0];
            valuesArray[1] = sensorArray[1];
            valuesArray[2] = sensorArray[2];
            locationBundle.setAccelerometerData(valuesArray);
            locationBundleArray[dataPointCounter] = locationBundle;
            //locationBundleArray[dataPointCounter].setAccelerometerData(accelerometerModule.getAccelerometerValues());
            dataPointCounter++;
        } else {
            dataPointCounter = 0;
            stop();
            //LocationBundle[] processArray = AccelAccumUtil.copyArray(locationBundleArray);
            //new Handler().post(new MapRunnable());
            mapRunnable.setLocationArray(locationBundleArray);
            mapHandler.post(mapRunnable);
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
