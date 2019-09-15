package com.my.bielik.task2.map;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.my.bielik.task2.R;
import com.my.bielik.task2.main.MainActivity;

import java.util.Locale;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MapFragment extends androidx.fragment.app.Fragment implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final float INITIAL_ZOOM = 12f;

    private GoogleMap map;
    private Marker currentMarker;
    private FloatingActionButton floatingActionButton;

    public interface OnPlaceSelectedCallback {
        void onPlaceSelected(double latitude, double longitude);
    }

    public MapFragment() {
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        floatingActionButton = view.findViewById(R.id.fab_search);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentMarker == null) {
                    Toast.makeText(getActivity(), "Place a marker", Toast.LENGTH_SHORT).show();
                } else {
                    LatLng latLng = currentMarker.getPosition();
                    ((MainActivity) getActivity()).onPlaceSelected(latLng.latitude, latLng.longitude);
                }
            }
        });
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        setMapLongClick(map);
        enableMyLocation(map);
    }

    private void enableMyLocation(GoogleMap map) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, INITIAL_ZOOM));
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(50.431782, 30.516382), INITIAL_ZOOM));
        }
    }
    private void setMapLongClick(final GoogleMap map) {
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                String snippet = String.format(Locale.getDefault(),
                        getString(R.string.lat_long_snippet),
                        latLng.latitude,
                        latLng.longitude);

                if (currentMarker != null) {
                    currentMarker.remove();
                }

                currentMarker = map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(getString(R.string.dropped_pin))
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.defaultMarker
                                (BitmapDescriptorFactory.HUE_BLUE)));
            }
        });
    }
}
