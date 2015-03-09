package treadsetters.bikesmart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Activity which starts an intent for either the logged in (MainActivity) or logged out
 * (SignUpOrLoginActivity) activity.
 */
public class DispatchActivity extends Activity {

    public static String MYTAG = "MYTAG";
    public static ArrayList<String> bikes_used;

  public DispatchActivity() {
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Check if there is current user info
    if (ParseUser.getCurrentUser() != null) {
      // Start an intent for the logged in activity
        Log.d("MYTAG", "User already logged in");

        // initialize database values...
        ParseUser current_user = ParseUser.getCurrentUser();
        if(current_user.get("bikes_used") == null){
            bikes_used = new ArrayList<String>();
            current_user.put("bikes_used",bikes_used);

        }

      startActivity(new Intent(this, MainActivity2.class));
    } else {
      // Start and intent for the logged out activity
      startActivity(new Intent(this, WelcomeActivity.class));
    }
  }

}
