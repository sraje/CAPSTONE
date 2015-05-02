package treadsetters.bikesmart;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Joel on 1/30/15.
 */

public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected Location mCurrentLocation;
    private static final String TAG = "LocationService";
    private final IBinder myBinder = new LocalBinder();
    private ArrayList<Location> recentLocations = new ArrayList<Location>();

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;

    @Override
    public void onCreate() {
        super.onCreate();
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return myBinder;
    }

    public class LocalBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        recentLocations.add(location);
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        Toast.makeText(this, "Location Updated",
                Toast.LENGTH_SHORT).show();
    }

    public Location getCurrentLocation() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        ParseUser user = ParseUser.getCurrentUser();

        Log.d(TAG, "2");
        //user.add("location_history", mLastLocation);
        user.add("location_history", "hello");
        Log.d(TAG, "3");

        user.saveInBackground();

        return mLastLocation;
    }

    public ArrayList<Location> getRecentLocations(){
        ArrayList<Location> tempLocations = new ArrayList<Location>(recentLocations);
        recentLocations.clear();
        return tempLocations;
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        Log.i(TAG, "Connection successful");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }
}
