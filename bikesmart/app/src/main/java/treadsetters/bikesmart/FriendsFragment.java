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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
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
 * {@link FriendsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button button_add_friend;

    public ExpandableListAdapter listAdapter;
    public ExpandableListView expListView;
    List<String> friendHeader;
    HashMap<String, List<String>> friendList;
    List <String> myFriends;
    boolean friendNameExists;
    boolean nameFound;
    ArrayList<String> allUsers;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FriendsFragment() {
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
        // Add Friend Button and dialog
        final View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        Button button_add_friend = (Button) rootView.findViewById(R.id.button_add_friend);
        button_add_friend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final EditText input = (EditText) rootView.findViewById(R.id.friend_name);

                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View v = inflater.inflate(R.layout.add_friends, null);
                builder.setView(v);
                builder.setTitle(R.string.add_friend);

                // Add action buttons
                builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText e = (EditText) v.findViewById(R.id.friend_name);
                        String friendName = e.getText().toString();
                        addFriendToParse(friendName);
                        Application.sendPushNotification(friendName,
                                ParseUser.getCurrentUser() + " has added you as a friend!");
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                builder.create();
                builder.show();
            }
        });

        // Friend List Accordian

        friendHeader = new ArrayList<String>();
        friendList = new HashMap<String, List<String>>();
        friendHeader.add("My Friends");

        getFriendList();
        getUsers();

        expListView = (ExpandableListView) rootView.findViewById(R.id.friend_list);
        listAdapter = new ExpandableListAdapter(getActivity(), friendHeader, friendList);
        expListView.setAdapter(listAdapter);
        expListView.expandGroup(0);

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void getFriendList() {
        myFriends = ParseUser.getCurrentUser().getList("friends");
        if (myFriends == null) {
            myFriends = new ArrayList();
        }
        friendList.put(friendHeader.get(0), myFriends);
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

    public void addFriendToParse(final String friendName) {
        // Catch Duplicates
        if(myFriends.contains(friendName)) {
            Toast.makeText(getActivity(), "User is already your friend!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Search for username, this clears the bikelist locally for some reason
       friendNameExists = doesUserExist(friendName);
        //friendNameExists = true;

        if (!friendNameExists){
            Toast.makeText(getActivity(), "User does not exist.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add friend to parse.
        // TODO: fix ugly remove/add code
        myFriends.add(friendName);
        ParseUser.getCurrentUser().remove("friends");
        ParseUser.getCurrentUser().addAll("friends", myFriends);

        // Save the post and return. Also show "Friend Added" Toast
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getActivity(), "Friend \"" + friendName + "\" Successfully Added!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(),
                            "Error saving: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        });

        // Let's refresh the list when we add people too.
        listAdapter.notifyDataSetChanged();
    }

    public boolean doesUserExist(final String friendName) {
        nameFound = false;
        // Let's add em if they're real
        if (allUsers.contains(friendName))
            nameFound = true;

        if (nameFound) return true;
        else return false;
    }

    public void getUsers() {
        allUsers  = new ArrayList<String>();
        allUsers.clear();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> postList, ParseException e) {
                if (e == null && postList.size() > 0) {
                    // Get list of usernames and make sure this user actually exists
                    for (ParseObject o : postList) {
                        allUsers.add(o.get("username").toString());
                    }
                } else {
                    Log.d("Friends", "Post retrieval failed...");
                }
            }
        });
    }
}
