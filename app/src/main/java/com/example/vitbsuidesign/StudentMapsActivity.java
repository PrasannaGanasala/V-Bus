package com.example.vitbsuidesign;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.vitbsuidesign.databinding.ActivityStudentMapsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StudentMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Geocoder geocoder;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationManager locationManager;
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location lastLocation = locationResult.getLastLocation();
        }
    };

    HashMap<String, Marker> busMarkers;

    private DatabaseReference databaseReference;

    private ActivityStudentMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStudentMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = FirebaseDatabase.getInstance().getReference();
        busMarkers = new HashMap<>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geocoder = new Geocoder(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(StudentMapsActivity.this, "Location Permissions Not Given", Toast.LENGTH_SHORT).show();
            return;
        }
        mMap.setMyLocationEnabled(true);

        databaseReference.child("Buses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    String busNo = dataSnapshot.getKey();
                    Double latitude = snapshot.child(busNo).child("Latitude").getValue(Double.class);
                    Double longitude = snapshot.child(busNo).child("Longitude").getValue(Double.class);

                    Iterator<Map.Entry<String, Marker>>
                            iterator = busMarkers.entrySet().iterator();

                    boolean keyFound = false;
                    Marker newMarker;

                    while (iterator.hasNext()) {
                        Map.Entry<String, Marker>
                                entry
                                = iterator.next();

                        if (busNo == entry.getKey()) {
                            keyFound = true;
                            break;
                        }
                    }


                    if(keyFound) {
                        newMarker = busMarkers.get(busNo);
                    }else {
                        newMarker = null;
                        busMarkers.put(busNo, null);
                    }

                    if (latitude != null && longitude != null) {
                        LatLng latLng = new LatLng(latitude, longitude);
                        newMarker = setBusMarker(latLng, busNo, newMarker);
                        busMarkers.put(busNo, newMarker);
                    } else {
                        Toast.makeText(StudentMapsActivity.this, "Null values", Toast.LENGTH_SHORT).show();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private Marker setBusMarker(LatLng latLng, String busNo, Marker busLocationMarker) {

        Location busCurrentLocation = new Location(LocationManager.GPS_PROVIDER);
        busCurrentLocation.setLatitude(latLng.latitude);
        busCurrentLocation.setLongitude(latLng.longitude);
        if (busLocationMarker == null) {
            //create new marker
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.yellow_car))
                    .title(busNo)
                    .anchor(0.5f, 0.5f)
                    .rotation(busCurrentLocation.getBearing());
            busLocationMarker = mMap.addMarker(markerOptions);
        } else {
            busLocationMarker.setPosition(latLng);
            busLocationMarker.setRotation(busCurrentLocation.getBearing());
        }

        return busLocationMarker;
    }
}