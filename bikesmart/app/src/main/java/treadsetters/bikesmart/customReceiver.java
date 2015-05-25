package treadsetters.bikesmart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Duncan on 5/18/2015.
 */
class CustomReceiver extends BroadcastReceiver {
    Listener listener = null;

    public interface Listener {
        public void onLocationChanged();
    }

    public void setListener(Listener l) {
        this.listener = l;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(listener!=null)
        {
            listener.onLocationChanged();
        }
    }
}