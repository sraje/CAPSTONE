package treadsetters.bikesmart;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

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
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
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

        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        Button buttonLogout = (Button) rootView.findViewById(R.id.button_logout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Perform action on click
                Toast.makeText(getActivity(), "Logging out...", Toast.LENGTH_SHORT).show();
                ParseUser.getCurrentUser().logOut();
                Intent intent = new Intent(getActivity(), DispatchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        Button buttonAddBike = (Button) rootView.findViewById(R.id.button_add_bike);
        buttonAddBike.setOnClickListener(new View.OnClickListener() {
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
                        String bikename = e.getText().toString();
                        Toast.makeText(getActivity(), "Bikename: " + bikename, Toast.LENGTH_SHORT).show();
                        addBikeToParse(bikename);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                builder.create();
                builder.show();


//                Button add_pic = (Button) rootView.findViewById(R.id.add_pic);
//                add_pic.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View view) {
//                        // Perform action on click
//                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                    }
//                });


            }
            // Perform action on click
        });
        Button buttonFindBike = (Button) rootView.findViewById(R.id.button_find_bikes);
        buttonFindBike.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Perform action on click
                Toast.makeText(getActivity(), "Finding bikes...", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    public void addBikeToParse(String bike_name) {
        double bike_id = 32.23; // dummy bike ID value

        ParseUser current_user = ParseUser.getCurrentUser();
        // Get the user's old list of bikes
        ArrayList<Double> user_bikes = (ArrayList<Double>) current_user.get("bikes_used");
        // Add the new bike to the list
        user_bikes.add(bike_id);
        current_user.put("my_groups", user_bikes);
        current_user.saveInBackground();

        // Create a new bike object
        ParseObject new_bike = new ParseObject("bike");
        new_bike.put("bikename", bike_name);
        new_bike.put("bikeID", bike_id);

        // Save the post and return
        new_bike.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null) {
//                    setResult(RESULT_OK);
//                    finish();
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
