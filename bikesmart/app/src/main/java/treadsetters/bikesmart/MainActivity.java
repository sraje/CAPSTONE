package treadsetters.bikesmart;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.Parse;




public class MainActivity extends ActionBarActivity {

    public static String key1 = "Iy2F0D1cUhwVVeVBt9Akg6ovargei7nIZqUItdr0";
    public static String key2 = "8rUgARpUSv8v1N7a7sN1DeLlnact6mITLLC4Bty5";

    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, key1, key2);

        Context context = getApplicationContext();
        NetworkStuff networkStuff = new NetworkStuff(context);
        isConnected = networkStuff.getConnectionStatus();
        // For testing purposes
        //Log.d("isConnected?", isConnected);
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
}
