package treadsetters.bikesmart;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.parse.ParseUser;


public class MainActivity2 extends ActionBarActivity
        implements HomeFragment.OnFragmentInteractionListener, BikesFragment.OnFragmentInteractionListener, FriendsFragment.OnFragmentInteractionListener, MessagesFragment.OnFragmentInteractionListener, NotificationsFragment.OnFragmentInteractionListener{
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }
    //First We Declare Titles And Icons For Our Navigation Drawer List View
    //This Icons And Titles Are holded in an Array as you can see

    String TITLES[] = {"Home","Bikes","Friends","Messages","Notifications"};
    int ICONS[] = {R.drawable.ic_home,R.drawable.ic_bikes,R.drawable.ic_friends,R.drawable.ic_messages,R.drawable.ic_notifications};

    //Similarly we Create a String Resource for the name and email in the header view
    //And we also create a int resource for profile picture in the header view

    String NAME; //= "Saili Raje";
    String EMAIL;// = "capstoned@gmail.com";
    int PROFILE = R.drawable.avi;

    private Toolbar toolbar;                              // Declaring the Toolbar Object



    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout

    ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar Drawer Toggle




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity2);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new HomeFragment())
                    .commit();
        }

    /* Assinging the toolbar object ot the view
    and setting the the Action bar to our toolbar
     */
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        NAME = ParseUser.getCurrentUser().getUsername();
        EMAIL = ParseUser.getCurrentUser().getEmail();



        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View

        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        mAdapter = new MyAdapter(TITLES,ICONS,NAME,EMAIL,PROFILE);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture

        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView

        final GestureDetector mGestureDetector = new GestureDetector(MainActivity2.this, new GestureDetector.SimpleOnGestureListener() {

            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });


        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(),motionEvent.getY());



                if(child!=null && mGestureDetector.onTouchEvent(motionEvent)){
                    Drawer.closeDrawers();

                    //Toast.makeText(MainActivity2.this,"The Item Clicked is: "+recyclerView.getChildPosition(child),Toast.LENGTH_SHORT).show();
                    int position = recyclerView.getChildPosition(child);
                    Fragment fragment;
                    //fragment = null;
                    FragmentManager fragmentManager = getFragmentManager(); // For AppCompat use getSupportFragmentManager
                    switch(position) {
                        default:
                        case 0:
                            //fragment = new MyFragment1();
                            break;
                        case 1://is home
                            fragment = new HomeFragment();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, fragment)
                                    .commit();
                            break;
                        case 2:
                            fragment = new BikesFragment();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, fragment)
                                    .commit();

                            //Toast.makeText(MainActivity2.this,"Bikess???",Toast.LENGTH_SHORT).show();

                            break;
                        case 3:
                            fragment = new FriendsFragment();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, fragment)
                                    .commit();

                            //Toast.makeText(MainActivity2.this,"Bikess???",Toast.LENGTH_SHORT).show();

                            break;

                        case 4:
                            fragment = new MessagesFragment();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, fragment)
                                    .commit();

                            //Toast.makeText(MainActivity2.this,"Bikess???",Toast.LENGTH_SHORT).show();

                            break;
                        case 5:
                            fragment = new NotificationsFragment();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, fragment)
                                    .commit();

                            //Toast.makeText(MainActivity2.this,"Bikess???",Toast.LENGTH_SHORT).show();

                            break;
                    }







                    return true;

                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }
        });


        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager


        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }



        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity2, menu);
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
            Log.d("MYTAG", "Hit settings...");
            return true;
        }
        if(id ==R.id.action_logout){
            ParseUser.getCurrentUser().logOut();
            Intent intent = new Intent(MainActivity2.this, DispatchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}