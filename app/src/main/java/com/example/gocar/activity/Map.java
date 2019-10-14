package com.example.gocar.activity;

import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import com.example.gocar.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        int position = Vehicles.carIndex;

        LatLng car = new LatLng(Vehicles.sortedCars.get(position).getLatitude(),Vehicles.sortedCars.get(position).getLongitude());
       // LatLng car = new LatLng(Vehicles.userLatitude,Vehicles.userLongitude);
        mMap.addMarker(new MarkerOptions().position(car).title(Vehicles.sortedCars.get(position).getName() + " Location, Lat: " + Vehicles.sortedCars.get(position).getLatitude() + ", Long: " + Vehicles.sortedCars.get(position).getLongitude()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(car));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(car,16));
    }
}
