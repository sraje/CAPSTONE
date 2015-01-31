package treadsetters.bikesmart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseUser;

/**
 * Activity which starts an intent for either the logged in (MainActivity) or logged out
 * (SignUpOrLoginActivity) activity.
 */
public class DispatchActivity extends Activity {

    public static String key1 = "Iy2F0D1cUhwVVeVBt9Akg6ovargei7nIZqUItdr0";
    public static String key2 = "8rUgARpUSv8v1N7a7sN1DeLlnact6mITLLC4Bty5";

  public DispatchActivity() {
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

      // Enable Local Datastore.
      Parse.enableLocalDatastore(this);
      Log.d("MYTAG", "enabled");
      Parse.initialize(this, key1, key2);

    // Check if there is current user info
    if (ParseUser.getCurrentUser() != null) {
      // Start an intent for the logged in activity
      startActivity(new Intent(this, MainActivity.class));
    } else {
      // Start and intent for the logged out activity
      startActivity(new Intent(this, WelcomeActivity.class));
    }
  }

}
