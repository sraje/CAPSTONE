package treadsetters.bikesmart;
import com.parse.Parse;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.parse.ParsePush;
import com.parse.SaveCallback;
import com.parse.ParseException;


import com.parse.ParseObject;

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

      ParsePush.subscribeInBackground("", new SaveCallback() {
          @Override
          public void done(ParseException e) {
              if (e == null) {
                  Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
              } else {
                  Log.e("com.parse.push", "failed to subscribe for push", e);
              }
          }
      });

  }


}
