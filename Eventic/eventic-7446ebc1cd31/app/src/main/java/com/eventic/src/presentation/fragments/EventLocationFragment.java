package com.eventic.src.presentation.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.eventic.src.presentation.activities.event.EventActivity;
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


public class EventLocationFragment extends Fragment {
    GoogleMap googleMap;
    FusedLocationProviderClient client;

    public EventLocationFragment() {
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
                                //Add marker on map
                                //Initialize marker options
                                MarkerOptions markerOptions = new MarkerOptions();
                                LatLng userLoc = new LatLng(location.getLatitude(), location.getLongitude());
                                LatLng eventLoc = ((EventActivity) getActivity()).getLocation();
                                if(eventLoc.longitude == 0.0 && eventLoc.latitude == 0.0) {
                                    ((EventActivity)getActivity()).showError("Error creating the event", "You didn't set a location for your event");
                                    return;
                                }
                                String eventTitle = ((EventActivity) getActivity()).getEventTitle();
                                ((EventActivity) getActivity()).setUserLocation(userLoc);
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(eventLoc)      // Sets the center of the map to Mountain View
                                        .zoom(12)                   // Sets the zoom
                                        .build();                   // Creates a CameraPosition from the builder
                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                markerOptions.title(eventTitle);
                                //Set position of marker
                                markerOptions.position(eventLoc);
                                googleMap.addMarker(markerOptions);
                                ((EventActivity) getActivity()).setDistance(((EventActivity) getActivity()).getDistance(eventLoc.latitude, ((EventActivity) getActivity()).getUserLatitude() , eventLoc.longitude, ((EventActivity) getActivity()).getUserLongitude() ,0,0) + " km");

                            }
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
                else{
                    requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 44);
                }

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
                    }
                });
            }

        });
        return view;
    }


}