package treadsetters.bikesmart;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity{

    private static final String TAG = "BikeSmart";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLatitudeText = (TextView) findViewById((R.id.latitude_text));
        mLongitudeText = (TextView) findViewById((R.id.longitude_text));

        Intent intent = new Intent(this, LocationService.class);
        ComponentName myService = startService(intent);
        bindService(new Intent(intent), myConnection, Context.BIND_AUTO_CREATE);

        final Button button = (Button) findViewById(R.id.start_location_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startLocationUpdates();
            }
        });

        final Button button2 = (Button) findViewById(R.id.location_button);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                displayLocation();
                ArrayList<Location> llist = getRecentLocations();
                for(Location l : llist){
                    continue;
                }
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Location getCurrentLocation() {
        Location currentLocation = myService.getCurrentLocation();
        return currentLocation;
    }

    private ArrayList<Location> getRecentLocations(){
        ArrayList<Location> l = myService.getRecentLocations();
        return l;
    }
}
