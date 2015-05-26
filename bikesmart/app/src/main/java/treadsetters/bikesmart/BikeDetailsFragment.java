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
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Duncan Sommer on 4/5/2015.
 */


public class BikeDetailsFragment extends Fragment implements OnMapReadyCallback, CustomReceiver.Listener
{
    private static final String TAG = "Bike Details";

    ParseObject bike;

    protected GoogleMap mMap;
    protected Marker bikeMarker;
    protected TextView distance_traveled_text_box;

    protected Location mLastLocation;
    protected LatLng mLastLatLng = new LatLng(34.4125, -119.8481);
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
        final TextView last_seen_text = (TextView) V.findViewById(R.id.last_seen);
        final TextView distance_traveled_text = (TextView) V.findViewById(R.id.distance_traveled);

        // Get the bike id from passed arguments
        final Double bike_id = this.getArguments().getDouble("bike_id");
        // Get the bike object from parse
        ParseQuery<ParseObject> query = ParseQuery.getQuery("bike");
        query.whereEqualTo("bike_id", bike_id);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> postList, ParseException e) {
                if (e == null && postList.size() > 0) {
                    Log.d(TAG, "bike found");
                    bike = postList.get(0);
                    bike_name_text.setText(bike.getString("bike_name"));
                    description_text.setText("Description: " + bike.getString("bike_description"));
                    //String last_user = bike.getString("")
                    //last_used_text.setText("Last used by: " + );
                    //String owner = bike.getString("owner_id")
                } else {
                    Log.d(TAG,"Post retrieval failed...");
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


        map.moveCamera( CameraUpdateFactory.newLatLngZoom(mLastLatLng, 14.0f) );

        bikeMarker = map.addMarker(new MarkerOptions()
                .position(mLastLatLng)
                .title("Last known bike location"));

    }

    public void onLocationChanged() {
        // FIX THIS
        Location location = null;

        if (location != null) {
            if (mLastLocation != null) {
                distance_traveled += mLastLocation.distanceTo(location);
            }

            mLastLocation = location;
            mLastLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            distance_traveled_text_box.setText(Float.toString(distance_traveled / 1000));
            animateMarker(bikeMarker, mLastLatLng);
        } else {
            Log.d(TAG, "Location Not Available");
        }
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