package treadsetters.bikesmart;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.text.DecimalFormat;
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
                    // Update bike name, details, and distance traveled
                    bike = postList.get(0);
                    bike_name_text.setText(bike.getString("bike_name"));
                    description_text.setText("Description: " + bike.getString("bike_description"));
                    Double km_traveled = bike.getDouble("dist_traveled") / 1000.0;
                    String dist_string = (new DecimalFormat("#.#").format(km_traveled));
                    distance_traveled_text.setText("Distance Traveled: " +
                                                    dist_string.toString() +
                                                    " km");

                    final Double last_user_id = bike.getDouble("last_user");
                    Double owner_id = bike.getDouble("owner_id");

                    // Update the Last User field
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

                    // Update the Owner field
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

                    // Update the map
                    if(bikeMarker != null) {
                        ParseGeoPoint bikeGeoPoint = (ParseGeoPoint) bike.get("current_loc");
                        current_location = new LatLng(bikeGeoPoint.getLatitude(), bikeGeoPoint.getLongitude());

                        bikeMarker.setPosition(current_location);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_location, 14.0f));
                    }

                    // Get an address string from current_location
                    Geocoder geocoder = new Geocoder(getActivity());
                    try {
                         List <Address> addresses = geocoder.getFromLocation(
                                                              current_location.latitude,
                                                              current_location.longitude,
                                                              1); //Ask for only 1 address
                         if(addresses != null) {
                             current_location_text.setText("Current Location: " +
                                                           addresses.get(0).getAddressLine(0) );
                         }
                    } catch (Exception x) {
                        Log.d(TAG, x.toString());
                    }

                }
            }
        });


        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if(mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
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

    public void onDestroyView() {
        super.onDestroyView();
        FragmentManager fm = getActivity().getFragmentManager();
        Fragment fragment = (fm.findFragmentById(R.id.map));
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
        ft.commit();
    }
}