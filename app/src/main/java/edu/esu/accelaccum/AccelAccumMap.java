package edu.esu.accelaccum;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton;

import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import edu.esu.accelaccum.module.LocationModule;

public class AccelAccumMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    ToggleButton trackingToggle;
    LocationModule locationModule;
    TextView testView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accel_accum_map);
        testView = (TextView) findViewById(R.id.testView);
        trackingToggle = (ToggleButton) findViewById(R.id.trackToggle);
        trackingToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startTrack();
                } else {
                    stopTrack();
                }
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void startTrack() {
        locationModule = new LocationModule(this, mMap);
        if (checkGPSStatus()) {
            try {
                locationModule.start();
            } catch (SecurityException se) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }
        }
        else {
            Toast.makeText(this, "GPS not turned on.", Toast.LENGTH_LONG).show();
            trackingToggle.setChecked(false);
        }
    }

    private void stopTrack() {
        locationModule.stop();
    }

    private boolean checkGPSStatus() {
        return locationModule.isEnabled();
    }
}
