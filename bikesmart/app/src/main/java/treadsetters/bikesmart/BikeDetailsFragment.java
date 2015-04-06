package treadsetters.bikesmart;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.parse.Parse;
import com.parse.ParseUser;
import java.util.ArrayList;

/**
 * Created by Duncan on 4/5/2015.
 */



public class BikeDetailsFragment extends Fragment {
    private static final String TAG = "Bike Details";

    protected Location mLastLocation;
    protected TextView mLatitudeText;
    protected TextView mLongitudeText;

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

    public BikeDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        // Inflate the layout for this fragment
        View V = inflater.inflate(R.layout.fragment_bike_details, container, false);
        mLatitudeText = (TextView) V.findViewById((R.id.latitude_text));
        mLongitudeText = (TextView) V.findViewById((R.id.longitude_text));

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

    private void startLocationUpdates(){
        myService.startLocationUpdates();
    }

    private void displayLocation(){
        mLastLocation = getCurrentLocation();
        if(mLastLocation != null) {
            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
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
}
