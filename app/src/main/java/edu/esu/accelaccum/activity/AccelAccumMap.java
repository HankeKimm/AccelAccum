package edu.esu.accelaccum.activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton;

import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import edu.esu.accelaccum.R;
import edu.esu.accelaccum.fragment.EmailDialogFragment;
import edu.esu.accelaccum.map.LocationModule;

public class AccelAccumMap extends FragmentActivity implements OnMapReadyCallback, EmailDialogFragment.EmailDialogListener {

    private GoogleMap mMap;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;

    private ToggleButton trackingToggle;
    private LocationModule locationModule;
    private TextView testView;

    private final String[] drawerListArray = {"Send Map to Email", "Observe Traveller"};

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
        inflateDrawer();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
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

    private void inflateDrawer() {
        mDrawerList = (ListView)findViewById(R.id.left_drawer);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, drawerListArray);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener(drawerListArray));
    }

    @Override
    public void onFinishEnterEmail(String inputText) {
        Intent mailClient = new Intent(android.content.Intent.ACTION_SEND);
        mailClient.setType("plain/text");
        mailClient.setData(Uri.parse("test@gmail.com"));
        mailClient.putExtra(Intent.EXTRA_EMAIL, new String[] {inputText});
        startActivity(mailClient);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        private String[] drawerListArray;

        public DrawerItemClickListener(String[] drawerListArray) {
            this.drawerListArray = drawerListArray;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String itemName = (String) parent.getItemAtPosition(position);
            for (int drawerListIndex = 0; drawerListIndex < drawerListArray.length; drawerListIndex++) {
                switch (itemName) {
                    case "Send Map to Email": {
                        showEmailDialog(position);
                    }
                    case "Observe Traveller": {
                        launchObserveMapActivity();
                    }
                }
            }
        }
    }

    private void launchObserveMapActivity() {
        Intent observeMapIntent = new Intent(this, AccelAccumObserverMapActivity.class);
        startActivity(observeMapIntent);
    }


    private void showEmailDialog(int position) {
        EmailDialogFragment fragment = new EmailDialogFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragment.show(fragmentManager, "email_dialog");
        mDrawerList.setItemChecked(position, true);
    }
}
