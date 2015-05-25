package treadsetters.bikesmart;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BikesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BikesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BikesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static String MYTAG = "Bikes";
    public static ArrayList<ParseObject> global_postList;
    public static ArrayAdapter<String> adapter;
    static final int SELECT_FILE = 201;
    public ExpandableListAdapter listAdapter;
    public ExpandableListView expListView;
    List<String> bikeHeaders;
    HashMap<String, List<String>> bikeLists;
    List<String> bikesOwned;
    List<String> bikesUsed;

    ParseObject sharedBike;
    boolean shared;
    ImageView button_plus;
    RoundImage roundedImage_plus;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BikesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BikesFragment newInstance(String param1, String param2) {
        BikesFragment fragment = new BikesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public BikesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_bikes, container, false);
        button_plus = (ImageView) rootView.findViewById(R.id.button_plus);
        Bitmap bm_locate = BitmapFactory.decodeResource(getResources(), R.drawable.plus);

        roundedImage_plus = new RoundImage(bm_locate);
        button_plus.setImageDrawable(roundedImage_plus);


        button_plus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final EditText input = (EditText) rootView.findViewById(R.id.bike_name);

                LayoutInflater inflater = getActivity().getLayoutInflater();
                final EditText bikenameEditText = new EditText(getActivity());
                final View v = inflater.inflate(R.layout.add_bike, null);
                builder.setView(v);
                builder.setTitle(R.string.add_bike);

                // Add action buttons
                builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
//                                String bikename = bikenameEditText.getText().toString().trim();
                        EditText e = (EditText) v.findViewById(R.id.bike_name);
                        EditText e2 = (EditText) v.findViewById(R.id.description);
                        //EditText e3 = (EditText) v.findViewById(R.id.bikeID);
                        String bikename = e.getText().toString();
                        String description = e2.getText().toString();
                        //String bikeID = e3.getText().toString();
                        Toast.makeText(getActivity(), "Bikename: " + bikename, Toast.LENGTH_SHORT).show();

                        addBikeToParse(bikename, description);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                builder.create();
                builder.show();


                Button add_pic = (Button) v.findViewById(R.id.add_pic);
                add_pic.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                       /* Intent intent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        startActivityForResult(
                                Intent.createChooser(intent, "Select File"),
                                SELECT_FILE);
//finish selecting file I guessss
                        TextView addbike = (TextView) v.findViewById(R.id.textView);
                        addbike.setVisibility(View.INVISIBLE);*/
                    }
                });


            }
            // Perform action on click
        });


        Log.d("MYTAG", "onCreateView");

        bikeHeaders = new ArrayList<String>();
        bikeLists = new HashMap<String, List<String>>();

        bikeHeaders.add("Bikes I Own");
        bikeHeaders.add("Bikes Shared With Me");

        bikesOwned = new ArrayList<String>();
        bikesUsed = new ArrayList<String>();
        expListView = (ExpandableListView) rootView.findViewById(R.id.bike_lists);

        getMyBikes();
        getSharedBikes();
        listAdapter = new ExpandableListAdapter(getActivity(), bikeHeaders, bikeLists);
        expListView.setAdapter(listAdapter);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.d(MYTAG, "onChildClick");

                ParseUser current_user = ParseUser.getCurrentUser();
                ArrayList<Double> bikes;

                if(groupPosition==0) {
                    bikes = (ArrayList<Double>) current_user.get("bikes_owned");
                } else {
                    bikes = (ArrayList<Double>) current_user.get("bikes_used");
                }
                Double bike_id = bikes.get(childPosition);
                Log.d(MYTAG, "childposition, bike_id = " + childPosition);
                Log.d(MYTAG, bike_id.toString());


                FragmentManager fragmentManager = getFragmentManager(); // For AppCompat use getSupportFragmentManager
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment fragment = new BikeDetailsFragment();
                Bundle args = new Bundle();

                args.putDouble("bike_id", bike_id); // Add the bike object here
                fragment.setArguments(args);

                // Switch to the bike details fragment.
                transaction.replace(R.id.container, fragment);
                // Make sure the user can press 'back'
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            }
        });

        expListView.setLongClickable(true);
        expListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {

                final long final_id = id;

                // Grab text from item clicked
                TextView name = (TextView) v.findViewById(R.id.bike_list_item);
                final String bikeName = name.getText().toString();
                // Show add friends alert dialog (same one from addfriends)
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View view = inflater.inflate(R.layout.add_friends, null);
                builder.setView(view);
                builder.setTitle("Would you like to Share or Delete this Bike?");
                // Add action buttons
                builder.setNegativeButton(R.string.share, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                      /*  EditText e = (EditText) view.findViewById(R.id.friend_name);
                        String friendName = e.getText().toString();
                        shareBike(friendName, bikeName);
                        Toast.makeText(getActivity(), "Bike Successfully shared with " + friendName + "!", Toast.LENGTH_SHORT).show();*/


                    }
                });
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete bike
                        int itemType = ExpandableListView.getPackedPositionType(final_id);
                        int childPosition;
                        int groupPosition;

                        if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                            childPosition = ExpandableListView.getPackedPositionChild(final_id);
                            groupPosition = ExpandableListView.getPackedPositionGroup(final_id);
                            Log.d("MYTAG", "Positions are " + childPosition + " " + groupPosition);
                            Log.d("MYTAG", "Bike is: " + bikesOwned.get(childPosition));
                            deleteBike(bikesOwned.get(childPosition));
                            Toast.makeText(getActivity(), bikeName + " successfully deleted!", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                            groupPosition = ExpandableListView.getPackedPositionGroup(final_id);
                            return;
                        } else {
                            return;
                        }
                    }

                }).create();


                builder.create();
                builder.show();

                return true;
            }

        });


        return rootView;




         /*Button buttonLogout = (Button) rootView.findViewById(R.id.button_refresh);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Perform action on click
                Toast.makeText(getActivity(), "Refreshing...", Toast.LENGTH_SHORT).show();
                getMyBikes();
            }
        });*/



    }
    public void deleteBike(String bike) {

        final String deleteBikeName = bike;
        Log.d("MYTAG", "Deleting bikes " + bike);

        final ParseUser current_user = ParseUser.getCurrentUser();
        ArrayList<Double> bikes_owned_copy = new ArrayList<Double>();
        ArrayList<String> bikes_used_copy = new ArrayList<String>();

        bikes_owned_copy = (ArrayList<Double>) current_user.get("bikes_owned");
        bikes_used_copy = (ArrayList<String>) current_user.get("bike_used");

        for (Double bike_id : bikes_owned_copy) {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("bike");
            Log.d(MYTAG, "bikeID is!! : " + bike_id);
            query.whereEqualTo("bike_id", bike_id);

            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> postList, ParseException e) {
                    if (e == null && postList.size() > 0) {
                        Log.d("MYTAG", "postList size: " + postList.size());
                        if(postList.get(0).getString("bike_name").toString().equals(deleteBikeName)) {
                            Log.d("MYTAG", "Found " + deleteBikeName + "!!!!");

                            postList.get(0).deleteInBackground();
                            Log.d("MYTAG", "Finished deleting bike.");
                            getMyBikes();

                        }


                    } else {
                        Log.d("MYTAG", "Post retrieval failed...");
                    }
                }
            });
        }

    }

    public void shareBike(final String friendName, String bikeName) {
        Log.d("AYY", friendName + " " + bikeName);

        // Get bike
        ParseQuery<ParseObject> query = ParseQuery.getQuery("bike");
        query.whereEqualTo("bike_name", bikeName);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> postList, ParseException e) {
                if (e == null && postList.size() > 0) {
                    sharedBike = postList.get(0);
                    sharedBike.add("access", friendName);

                    sharedBike.saveEventually();
                    shared = true;
                } else {
                    Log.d("MYTAG", "Post retrieval failed...");
                    shared = false;
                }
            }
        });

    }


    public void getMyBikes() {

        ParseUser current_user = ParseUser.getCurrentUser();

        ArrayList<Double> bikes_owned_copy = new ArrayList<Double>();

        bikes_owned_copy = (ArrayList<Double>) current_user.get("bikes_owned");

        bikesOwned.clear();

        for (Double bike_id : bikes_owned_copy) {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("bike");
            Log.d(MYTAG, "bikeID is!! : " + bike_id);
            query.whereEqualTo("bike_id", bike_id);

            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> postList, ParseException e) {
                    if (e == null && postList.size() > 0) {
                        bikesOwned.add(postList.get(0).getString("bike_name"));
                    } else {
                        Log.d("MYTAG","Post retrieval failed...");
                    }
                }
            });
        }

        bikeLists.put(bikeHeaders.get(0), bikesOwned);
    }

    public void getSharedBikes() {
        final String username=ParseUser.getCurrentUser().getUsername();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("bike");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> postList, ParseException e) {
                if (e == null && postList.size() > 0) {
                    // Get list of usernames and make sure this user actually exists
                    for (ParseObject o : postList) {
                        if (o.get("access") != null) {
                            if (o.get("access").toString().contains(username)) {
                                bikesUsed.add(o.get("bike_name").toString());
                            }
                        }
                    }
                    bikeLists.put(bikeHeaders.get(1), bikesUsed);
                } else {
                    Log.d("Friends", "Post retrieval failed...");
                }
            }
        });


    }

    public void addBikeToParse(String bikename, String description) {
        ParseUser current_user = ParseUser.getCurrentUser();
        ParseObject new_bike = new ParseObject("bike");
        new_bike.put("bike_name", bikename);


//        double bikeID = count;
        double bikeID = Math.random() * 1000000;


        Log.d("MYTAG", "bikeID: " + bikeID);
        new_bike.put("bike_id", bikeID);
        ArrayList<Double> temp_bikes_owned = new ArrayList<Double>();
        temp_bikes_owned = (ArrayList<Double>) current_user.get("bikes_owned");
        temp_bikes_owned.add(bikeID); // random bike ID value
        current_user.put("bikes_owned", temp_bikes_owned);

        //new_bike.put("bike_id", bikeID);
        new_bike.put("bike_description", description);
        new_bike.put("owner_id", current_user.get("user_id"));
        new_bike.put("current_loc", "");
        new_bike.put("private_flag", "false");
        new_bike.put("locked_flag", "false");

        Log.d("MYTAG", "bike_id: " + bikeID);

        current_user.saveInBackground();

        // Save the post and return
        new_bike.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getActivity(), "Bike Successfully Added!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getActivity(),
                            "Error saving: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }


        });


    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
