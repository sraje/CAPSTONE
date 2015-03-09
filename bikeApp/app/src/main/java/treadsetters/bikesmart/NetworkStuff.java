package treadsetters.bikesmart;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by karcher on 1/30/15.
 */
public class NetworkStuff {

    private Boolean isConnected = false;


    public NetworkStuff (Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        this.isConnected = (activeNetwork != null) &&
                activeNetwork.isConnectedOrConnecting();
    }

    public boolean getConnectionStatus() {
        return this.isConnected;
    }

}
