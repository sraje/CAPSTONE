package treadsetters.bikesmart;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParsePush;

import java.util.ArrayList;

public class Application extends android.app.Application {

    public static String PARSE_APPLICATION_ID = "Iy2F0D1cUhwVVeVBt9Akg6ovargei7nIZqUItdr0";
    public static String PARSE_CLIENT_KEY = "8rUgARpUSv8v1N7a7sN1DeLlnact6mITLLC4Bty5";

    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Log.d("MYTAG", "enabled");
        Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);

    }

    public static void sendPushNotification(String username, String message) {
        ArrayList<String> queryList = new ArrayList<String>();
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        queryList.add(username);
        query.whereContainedIn("username", queryList);

        //send Notification
        ParsePush push = new ParsePush();
        push.setQuery(query);
        push.setMessage(message);
        push.sendInBackground();


    }


}
