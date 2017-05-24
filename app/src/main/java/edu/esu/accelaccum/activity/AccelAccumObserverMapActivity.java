package edu.esu.accelaccum.activity;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import edu.esu.accelaccum.R;
import edu.esu.accelaccum.firebase.FirebaseUtil;
import edu.esu.accelaccum.map.LocationModule;
import edu.esu.accelaccum.model.MapPoint;

import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.*;

public class AccelAccumObserverMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    DatabaseReference dbReference;
    PolylineOptions mapPolyLineOptions;
    MapPoint previousMapPoint;
    private ToggleButton observerButton;

    private final String locationDbName = "locations";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accel_accum_observer_map);
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        final ToggleButton observerButton = (ToggleButton) findViewById(R.id.observeButton);
        mapFragment.getMapAsync(this);
        mapPolyLineOptions = new PolylineOptions();
        dbReference = FirebaseUtil.getFirebaseDatabaseReference().child(locationDbName);
        final ValueEventListener locationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MapPoint currentMapPoint = dataSnapshot.getValue(MapPoint.class);
                mMap.addPolyline(mapPolyLineOptions.add((previousMapPoint == null) ? currentMapPoint.getLatLng() : previousMapPoint.getLatLng(), currentMapPoint.getLatLng()));
                previousMapPoint = currentMapPoint;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        observerButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dbReference.addValueEventListener(locationListener);
                } else {
                    dbReference.removeEventListener(locationListener);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


}
