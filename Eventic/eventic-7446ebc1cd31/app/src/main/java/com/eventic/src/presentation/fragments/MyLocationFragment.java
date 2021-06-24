package com.eventic.src.presentation.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.eventic.src.domain.Event;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;


public class  MyLocationFragment extends Fragment {
    GoogleMap googleMap;
    FusedLocationProviderClient client;
    List<Event> events;
    interface JsonHttpApi {
        @GET("eventos")
        Call<List<Event>> getEvents();
    }



    private ArrayList<LatLng> locationArrayList;

    public MyLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //Initialize view
        View view = inflater.inflate(R.layout.fragment_my_location, container, false);

        //Initialize map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        client = LocationServices.getFusedLocationProviderClient(getActivity());

        locationArrayList = new ArrayList<>();

        // on below line we are adding our
        // locations in our array list.

        //Async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);


                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("https://eventic-api.herokuapp.com/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        MyLocationFragment.JsonHttpApi jsonHttpApi = retrofit.create(MyLocationFragment.JsonHttpApi.class);

                        Call<List<Event>> call = jsonHttpApi.getEvents();

                        call.enqueue(new Callback<List<Event>>() {
                            @Override
                            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {

                                List<Event> eventResponse = response.body();

                                if (!response.isSuccessful()) {
                                    System.out.println(response.message());
                                    return;
                                }
                                events = eventResponse;
                                for(int i = 0; i < eventResponse.size(); ++i){

                                    double d1;
                                    double d2;
                                    if((eventResponse.get(i).getLatitude()) == null){
                                        d1 = 0;
                                    }
                                    else d1 = Double.parseDouble((eventResponse.get(i).getLatitude()));

                                    if((eventResponse.get(i).getLongitude()) == null) {
                                        d2 = 0;
                                    }
                                    else d2 = Double.parseDouble((eventResponse.get(i).getLongitude()));
                                    LatLng l = new LatLng(d1, d2);
                                    locationArrayList.add(l);
                                    googleMap.addMarker(new MarkerOptions().position(locationArrayList.get(i)).title(eventResponse.get(i).getTitle()));

                                }
                            }
                            @Override
                            public void onFailure(Call<List<Event>> call, Throwable t) {

                                System.out.println("Connection FAILED");
                            }
                        });
                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            Intent intent = new Intent(getActivity(), EventActivity.class);
                            Integer id = null;
                            for (Event i : events) {
                                if(i.getTitle().equals(marker.getTitle())) id = i.getId();
                            }

                            intent.putExtra("id", id);
                            getActivity().startActivity(intent);
                            return false;
                        }
                    });


                    client.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                LatLng l = new LatLng(location.getLatitude(),location.getLongitude());
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(l)      // Sets the center of the map to Mountain View
                                        .zoom(17)                   // Sets the zoom
                                        .bearing(270)                // Sets the orientation of the camera to west
                                        .tilt(10)                   // Sets the tilt of the camera to x degrees
                                        .build();                   // Creates a CameraPosition from the builder
                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
            }

        });
        return view;
    }



}