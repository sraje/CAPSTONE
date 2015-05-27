package treadsetters.bikesmart;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
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
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Duncan Sommer on 4/5/2015.
 */


public class BikeDetailsFragment extends Fragment implements OnMapReadyCallback
{
    private static final String TAG = "Bike Details";

    ParseObject bike = null;

    protected GoogleMap mMap;
    protected Marker bikeMarker = null;

    protected LatLng current_location = new LatLng(34.4125, -119.8481);
    protected float distance_traveled = 0;


    public BikeDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View V = inflater.inflate(R.layout.fragment_bike_details, container, false);

        // Get all the TextView references
        final TextView bike_name_text = (TextView) V.findViewById(R.id.bike_name);
        final TextView description_text = (TextView) V.findViewById(R.id.description);
        final TextView last_used_text = (TextView) V.findViewById(R.id.last_used);
        final TextView owner_text = (TextView) V.findViewById(R.id.owner);
        final TextView current_location_text = (TextView) V.findViewById(R.id.current_location);
        final TextView distance_traveled_text = (TextView) V.findViewById(R.id.distance_traveled);

        // Get the bike id from passed arguments
        final Double bike_id = this.getArguments().getDouble("bike_id");
        // Get the bike object from parse
        ParseQuery<ParseObject> bike_query = ParseQuery.getQuery("bike");
        bike_query.whereEqualTo("bike_id", bike_id);
        bike_query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> postList, ParseException e) {
                if (e != null) {
                    Log.d(TAG, e.toString());
                } else if(postList.size()==0) {
                    Log.d(TAG, "No user bikes found.");
                } else {
                    Log.d(TAG, "bike found");
                    bike = postList.get(0);
                    bike_name_text.setText(bike.getString("bike_name"));
                    description_text.setText("Description: " + bike.getString("bike_description"));

                    final Double last_user_id = bike.getDouble("last_user");
                    Double owner_id = bike.getDouble("owner_id");

                    // Get the user's name from pase
                    ParseQuery<ParseUser> user_query = ParseUser.getQuery();
                    user_query.whereEqualTo("user_id", last_user_id);
                    user_query.findInBackground(new FindCallback<ParseUser>() {
                        public void done(List<ParseUser> postList, ParseException e) {
                            if (e != null) {
                                Log.d(TAG, e.toString());
                            } else if(postList.size()==0) {
                                Log.d(TAG, "No user bikes found.");
                            } else {
                                last_used_text.setText("Last used by: " + postList.get(0).getString("username"));
                            }
                        }
                    });

                    ParseQuery<ParseUser> owner_query = ParseUser.getQuery();
                    owner_query.whereEqualTo("user_id", owner_id);
                    owner_query.findInBackground(new FindCallback<ParseUser>() {
                        public void done(List<ParseUser> postList, ParseException e) {
                            if (e != null) {
                                Log.d(TAG, e.toString());
                            } else if(postList.size()==0) {
                                Log.d(TAG, "No user bikes found.");
                            } else {
                                owner_text.setText("Owner: " + postList.get(0).getString("username"));
                            }
                        }
                    });

                    if(bikeMarker != null) {
                        ParseGeoPoint bikeGeoPoint = (ParseGeoPoint) bike.get("current_loc");
                        current_location = new LatLng(bikeGeoPoint.getLatitude(), bikeGeoPoint.getLongitude());

                        bikeMarker.setPosition(current_location);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_location, 14.0f));
                    }
                }
            }
        });


        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if(mapFragment == null) { Log.d(TAG, "mapfrag==null"); }
        mapFragment.getMapAsync(this);

        return V;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d(TAG, "onMapReady");
        mMap = map;

        if(bike != null) {
            ParseGeoPoint bikeGeoPoint = (ParseGeoPoint) bike.get("current_loc");
            current_location = new LatLng(bikeGeoPoint.getLatitude(), bikeGeoPoint.getLongitude());
        }

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(current_location, 14.0f));
        bikeMarker = map.addMarker(new MarkerOptions()
                .position(current_location)
                .title("Last known bike location"));

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