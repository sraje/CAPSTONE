package treadsetters.bikesmart;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
    public static String MYTAG = "MYTAG";
    private ArrayList<String> mybikes;
    public static ArrayList<ParseObject> global_postList;
    public static ArrayAdapter<String> adapter;

    public ExpandableListAdapter listAdapter;
    public ExpandableListView expListView;
    List<String> bikeHeaders;
    HashMap<String, List<String>> bikeLists;
    List<String> bikesOwned;
    List<String> bikesUsed;

    ParseObject sharedBike;
    boolean shared;


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
        View rootView = inflater.inflate(R.layout.fragment_bikes, container, false);
        Log.d("MYTAG","onCreateView");

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

        expListView.setLongClickable(true);
        expListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                // Grab text from item clicked
                TextView name = (TextView)v.findViewById(R.id.bike_list_item);
                final String bikeName = name.getText().toString();

                // Show add friends alert dialog (same one from addfriends)
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View view = inflater.inflate(R.layout.add_friends, null);
                builder.setView(view);
                builder.setTitle(R.string.share_bike);

                // Add action buttons
                builder.setPositiveButton(R.string.share, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText e = (EditText) view.findViewById(R.id.friend_name);
                        String friendName = e.getText().toString();
                        shareBike(friendName, bikeName);
                        Toast.makeText(getActivity(), "Bike Successfully shared with " + friendName + "!", Toast.LENGTH_SHORT).show();
//                        else
//                            Toast.makeText(getActivity(), "Error sharing bike with " + friendName + "!", Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                builder.create();
                builder.show();
                return true;
            }

        });

         /*Button buttonLogout = (Button) rootView.findViewById(R.id.button_refresh);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Perform action on click
                Toast.makeText(getActivity(), "Refreshing...", Toast.LENGTH_SHORT).show();
                getMyBikes();
            }
        });*/



        return rootView;
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
        ArrayList<String> bikes_used_copy = new ArrayList<String>();

        bikes_owned_copy = (ArrayList<Double>) current_user.get("bikes_owned");
        bikes_used_copy = (ArrayList<String>) current_user.get("bike_used");

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

        /*for (String bike_id : bikes_used_copy) {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("bike");
            Log.d(MYTAG, "bikeID is!! : " + bike_id);
            query.whereEqualTo("bike_id", bike_id);

            // run query in foreground

            Log.d(MYTAG, "sending query");

            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> postList, ParseException e) {
                    if (e == null) {
                        bikesUsed.add(postList.get(0).getString("bike_name"));
                    } else {
                        Log.d("MYTAG","Post retrieval failed...");
                    }
                }
            });


        }*/

        bikeLists.put(bikeHeaders.get(0), bikesOwned);
        bikeLists.put(bikeHeaders.get(1), bikesUsed);
    }

    public void getSharedBikes() {
        final String username=ParseUser.getCurrentUser().getUsername();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("bike");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> postList, ParseException e) {
                if (e == null && postList.size() > 0) {
                    // Get list of usernames and make sure this user actually exists
                    for (ParseObject o : postList) {
                        if(o.get("access") != null) {
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
