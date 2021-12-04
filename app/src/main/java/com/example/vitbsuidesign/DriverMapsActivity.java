package com.example.vitbsuidesign;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.vitbsuidesign.databinding.ActivityDriverMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverMapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private ActivityDriverMapsBinding binding;

    String busNo;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private FusedLocationProviderClient mFusedLocationClient;
    //private Location location;
    private LocationManager locationManager;

    private Double latitude, longitude;
    private Double end_latitude = 12.8406;
    //TODO: update the end longitude with bus database
    private Double end_longitude = 80.1534;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDriverMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent  intent = getIntent();
        busNo = intent.getStringExtra("BusNo");

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Buses").child(busNo);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(DriverMapsActivity.this, "Location Permissions Not Given", Toast.LENGTH_SHORT).show();
            return;
        }
        mMap.setMyLocationEnabled(true);

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null) {
                    updateLocationFirebase(location);
                }
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,0, this::onLocationChanged);

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);


        updateLocationFirebase(location);

       /* Object dataTransfer[] = new Object[3];
        String url = getDirectionsUrl();
        GetDirectionsData getDirectionsData = new GetDirectionsData();
        dataTransfer[0] =  mMap;
        dataTransfer[1] = url;
        dataTransfer[2] = new LatLng(end_latitude, end_longitude);

        getDirectionsData.execute(dataTransfer);

        float[] results = new float[10];
        Location.distanceBetween(latitude, longitude, end_latitude, end_longitude, results);*/

    }


    private void updateLocationFirebase(Location location) {

        Toast.makeText(DriverMapsActivity.this, "LocationChanged", Toast.LENGTH_SHORT).show();

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15.8F));

        databaseReference.child("Latitude").setValue(location.getLatitude()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    //Toast.makeText(DriverMapsActivity.this, "Location Saved", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(DriverMapsActivity.this, "Location Not Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });
        databaseReference.child("Longitude").setValue(location.getLongitude()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    //Toast.makeText(DriverMapsActivity.this, "Location Saved", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(DriverMapsActivity.this, "Location Not Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateLocationFirebase(LatLng latLng) {

        Toast.makeText(DriverMapsActivity.this, "LocationChanged", Toast.LENGTH_SHORT).show();

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.8F));

        databaseReference.child("Latitude").setValue(latLng.latitude).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    //Toast.makeText(DriverMapsActivity.this, "Location Saved", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(DriverMapsActivity.this, "Location Not Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });
        databaseReference.child("Longitude").setValue(latLng.longitude).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    //Toast.makeText(DriverMapsActivity.this, "Location Saved", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(DriverMapsActivity.this, "Location Not Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //TODO: GET DIRECTIONS FOR BUS STOP
    /*@NonNull
    private String getDirectionsUrl() {
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin=" + latitude + "," + longitude);
        googleDirectionsUrl.append("&destination=" + end_longitude + "," + end_longitude);
        googleDirectionsUrl.append("&key=" + R.string.Gmaps_API_Key);
        return googleDirectionsUrl.toString();
    }*/
}