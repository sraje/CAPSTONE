package treadsetters.bikesmart;
// package com.example.mapdemo;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Fragment;
import android.util.Log;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.Parse;
import com.parse.ParseUser;
import java.util.ArrayList;

/**
 * Created by Duncan on 4/5/2015.
 */


public class BikeDetailsFragment extends Fragment implements OnMapReadyCallback {
    public BikeDetailsFragment() {
        // Required empty public constructor
    }

    private static final String TAG = "Bike Details";
    protected Location mLastLocation = null;
    protected LatLng mLastLatLng = new LatLng(34.4125, -119.8481);
    protected Marker bikeMarker = null;

    LocationService myService;

    volatile boolean isBound = false;

    private ServiceConnection myConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            myService = binder.getService();
            isBound = true;
        }

        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        // Inflate the layout for this fragment
        View V = inflater.inflate(R.layout.fragment_bike_details, container, false);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final Button start_location_button = (Button) V.findViewById(R.id.start_location_button);
        start_location_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startLocationUpdates();
            }
        });

        final Button button2 = (Button) V.findViewById(R.id.location_button);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                displayLocation();
//                ArrayList<Location> llist = getRecentLocations();
//                for(Location l : llist){
//                    continue;
//                }
            }
        });

        return V;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        Activity currentActivity = getActivity();
        Intent intent = new Intent(currentActivity, LocationService.class);
        ComponentName myService = currentActivity.startService(intent);
        currentActivity.bindService(new Intent(intent), myConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onMapReady(GoogleMap map) {

        map.moveCamera( CameraUpdateFactory.newLatLngZoom(mLastLatLng, 25.0f) );

        bikeMarker = map.addMarker(new MarkerOptions()
                                        .position(mLastLatLng)
                                        .title("Last known bike location"));

    }

    private void startLocationUpdates(){
        myService.startLocationUpdates();
    }


    private void displayLocation(){
        mLastLocation = getCurrentLocation();

        if(mLastLocation != null) {
            mLastLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            if(bikeMarker != null) {
                animateMarker(bikeMarker, mLastLatLng);
            }
        }
        else{
            Log.i(TAG, "Location Not Available");
        }
    }

    private Location getCurrentLocation() {
        Location currentLocation = myService.getCurrentLocation();
        return currentLocation;
    }

    private ArrayList<Location> getRecentLocations(){
        ArrayList<Location> l = myService.getRecentLocations();
        return l;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


    static LatLng interpolate(float fraction, LatLng a, LatLng b) {
        double lat = (b.latitude - a.latitude) * fraction + a.latitude;
        double lng = (b.longitude - a.longitude) * fraction + a.longitude;
        return new LatLng(lat, lng);
    }

    static void animateMarker(Marker marker, LatLng finalPosition) {
        TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
            @Override
            public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                return interpolate(fraction, startValue, endValue);
            }
        };
        Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");
        ObjectAnimator animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, finalPosition);
        animator.setDuration(3000);
        animator.start();
    }
}