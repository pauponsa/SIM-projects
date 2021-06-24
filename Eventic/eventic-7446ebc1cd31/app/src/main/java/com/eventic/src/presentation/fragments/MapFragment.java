package com.eventic.src.presentation.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventic.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;


public class MapFragment extends Fragment {
    GoogleMap googleMap;
    FusedLocationProviderClient client;
    private String latitude, longitude;



    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //Initialize view
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        //Initialize map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        client = LocationServices.getFusedLocationProviderClient(getActivity());

        // in below line we are initializing our array list.



        //Async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    googleMap.setMyLocationEnabled(true);

                    client.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {


                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                LatLng l = new LatLng(location.getLatitude(),location.getLongitude());
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(l)      // Sets the center of the map to Mountain View
                                        .zoom(14)                   // Sets the zoom
                                        .bearing(270)                // Sets the orientation of the camera to west
                                        .tilt(10)                   // Sets the tilt of the camera to x degrees
                                        .build();                   // Creates a CameraPosition from the builder
                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            }
                        }
                    });
                    //When map is loaded
                    googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            //When clicked on map
                            //Initialize marker options
                            MarkerOptions markerOptions = new MarkerOptions();
                            //Set position of marker
                            markerOptions.position(latLng);
                            //Set title of marker
                            markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                            //Remove all marker
                            googleMap.clear();
                            //Animating to zoom the marker
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                            //Add marker on map
                            googleMap.addMarker(markerOptions);
                            latitude =  String.valueOf(latLng.latitude);
                            longitude = String.valueOf(latLng.longitude);

                        }
                    });
                    return;
                }
                else if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                    // In an educational UI, explain to the user why your app requires this
                    // permission for a specific feature to behave as expected. In this UI,
                    // include a "cancel" or "no thanks" button that allows the user to
                    // continue using your app without granting the permission.
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                    alertDialogBuilder.setTitle(getText(R.string.permission_needed));
                    alertDialogBuilder
                            .setMessage(getText(R.string.need_location_permission))
                            .setCancelable(true);

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                    return;
                }
                else requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 44);
            }

        });
        return view;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}