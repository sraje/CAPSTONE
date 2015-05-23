package treadsetters.bikesmart;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends Activity{

    private static final String TAG = "BikeSmart";
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;

    protected Location mLastLocation;
    protected TextView mLatitudeText;
    protected TextView mLongitudeText;

    private BluetoothGattCharacteristic mGattCharacteristic;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private String mDeviceName;
    private String mDeviceAddress;
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    LocationService myService;
    volatile boolean isBound = false;

    boolean firstData = true;
    int lastWheelRot;
    long lastWheelTime;
    int lastCrankRot;
    int lastCrankTime;

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

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        final Button start_location_button = (Button) findViewById(R.id.start_location_button);
        start_location_button.setOnClickListener(new View.OnClickListener() {
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

        final Button logout_button = (Button) findViewById(R.id.logout_button);
        logout_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("MYTAG", "Logging user out.");
                ParseUser.getCurrentUser().logOut();
                startActivity(new Intent(v.getContext(), DispatchActivity.class));

            }
        });

        final Button bluetooth_button = (Button) findViewById(R.id.bluetooth_button);
        bluetooth_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(new Intent(v.getContext(), DeviceScanActivity.class), 1);
            }
        });


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            mDeviceName = data.getStringExtra(EXTRAS_DEVICE_NAME);
            mDeviceAddress = data.getStringExtra(EXTRAS_DEVICE_ADDRESS);

            Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        }
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

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private void displayData(String data, TextView txt) {
        if (data != null) {
            txt.setText(data);
        }
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            List<BluetoothGattCharacteristic> gattCharacteristics = new ArrayList<BluetoothGattCharacteristic>();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                for(BluetoothGattService service : mBluetoothLeService.getSupportedGattServices()){
                    if (service.getUuid().equals(UUID.fromString("00001816-0000-1000-8000-00805f9b34fb"))){
                        gattCharacteristics = service.getCharacteristics();
                    }
                }
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    if (gattCharacteristic.getUuid().equals(UUID.fromString("00002a5b-0000-1000-8000-00805f9b34fb"))){
                        mGattCharacteristic = gattCharacteristic;
                        if (mGattCharacteristic != null) {
                            final BluetoothGattCharacteristic characteristic = mGattCharacteristic;
                            final int charaProp = characteristic.getProperties();
                            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                                // If there is an active notification on a characteristic, clear
                                // it first so it doesn't update the data field on the user interface.
                                if (mNotifyCharacteristic != null) {
                                    mBluetoothLeService.setCharacteristicNotification(
                                            mNotifyCharacteristic, false);
                                    mNotifyCharacteristic = null;
                                }
                                mBluetoothLeService.readCharacteristic(characteristic);
                            }
                            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                                mNotifyCharacteristic = characteristic;
                                mBluetoothLeService.setCharacteristicNotification(
                                        characteristic, true);
                            }
                        }
                    }
                }
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                if(firstData){
                    lastWheelRot = intent.getIntExtra("WheelRev", 0);
                    lastWheelTime = intent.getIntExtra("WheelTime", 0);
                    lastCrankRot = intent.getIntExtra("CrankRev", 0);
                    lastCrankTime = intent.getIntExtra("CrankTime", 0);
                    firstData = false;
                }
                else{
                    int wheelRot = intent.getIntExtra("WheelRev", 0);
                    long wheelTime = intent.getIntExtra("WheelTime", 0);
                    int crankRot = intent.getIntExtra("CrankRev", 0);
                    int crankTime = intent.getIntExtra("CrankTime", 0);

                    int wheelRotDiff = wheelRot - lastWheelRot < 0 ? wheelRot + lastWheelRot % 65534 : wheelRot - lastWheelRot;
                    long wheelTimeDiff = wheelTime - lastWheelTime < 0 ? wheelTime + lastWheelTime % 4294967294L : wheelTime - lastWheelTime;
                    int crankRotDiff = crankRot - lastCrankRot < 0 ? crankRot + lastCrankRot % 65534 : crankRot - lastCrankRot;
                    int crankTimeDiff = crankTime - lastCrankTime < 0 ? crankTime + lastCrankTime % 65534 : crankTime - lastCrankTime;

                    lastWheelRot = wheelRot;
                    lastWheelTime = wheelTime;
                    lastCrankRot = crankRot;
                    lastCrankTime = crankTime;

                    double wheelRotPerSec = 0;
                    double crankRotPerSec = 0;
                    if(wheelTimeDiff != 0) {
                        wheelRotPerSec = wheelRotDiff * 1.0 / wheelTimeDiff * 1024;
                    }
                    if(crankTimeDiff != 0) {
                        crankRotPerSec = crankRotDiff*1.0/crankTimeDiff*1024;
                    }

                    TextView view = (TextView) findViewById(R.id.speed_cadence_text);
                    displayData("Wheel: " + Double.toString(wheelRotPerSec) + " Crank: " + Double.toString(crankRotPerSec), view);
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
