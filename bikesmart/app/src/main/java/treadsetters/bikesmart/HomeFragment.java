package treadsetters.bikesmart;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private static final int CHANGE_BIKE = 5;
    boolean lock = true;
    boolean light = false;
    ImageView imageView1;
    ImageView button_locate;
    ImageView button_lock;
    ImageView button_light;
    ImageView imageView;
    RoundImage roundedImage_def;
    RoundImage roundedImage_overlay;
    RoundImage roundedImage_location;
    RoundImage roundedImage_lock;
    RoundImage roundedImage_light;
    TextView activeBikeText;
    TextView changeDefaultBikeText;
    ParseUser current_user;
    Double currentDefaultBikeId;
    // TODO: Rename and change types of parameters
    static final int SELECT_FILE = 201;
    private String mParam1;
    private String mParam2;
    public int count = 0;

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
        Context context = getActivity();

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

        current_user = ParseUser.getCurrentUser();

        changeDefaultBikeText = (TextView) rootView.findViewById(R.id.defaultBikeText);

        activeBikeText = (TextView) rootView.findViewById(R.id.activebike_name);
        currentDefaultBikeId = current_user.getNumber("default_bike_id").doubleValue();
        if(currentDefaultBikeId != 0){
            ParseQuery<ParseObject> query = ParseQuery.getQuery("bike");
            query.whereEqualTo("bike_id", currentDefaultBikeId);

            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> postList, ParseException e) {
                    if (e == null && postList.size() > 0) {
                        activeBikeText.setText(postList.get(0).getString("bike_name"));
                    } else {
                        Log.d("MYTAG","Post retrieval failed...");
                    }
                }
            });
        }

        imageView1 = (ImageView)
                rootView.findViewById(R.id.imageView1);
        Bitmap bm = BitmapFactory.decodeResource(getResources(),R.drawable.def);

        roundedImage_def = new RoundImage(bm);
        imageView1.setImageDrawable(roundedImage_def);
/* overlay switch buke */
        imageView = (ImageView)
                rootView.findViewById(R.id.imageView);
        Bitmap bm1 = BitmapFactory.decodeResource(getResources(),R.drawable.overlay);

        roundedImage_overlay = new RoundImage(bm1);
        imageView.setImageDrawable(roundedImage_overlay);



        button_locate = (ImageView)
                rootView.findViewById(R.id.button_locate);
        Bitmap bm_locate = BitmapFactory.decodeResource(getResources(),R.drawable.locate);

        roundedImage_location = new RoundImage(bm_locate);
        button_locate.setImageDrawable(roundedImage_location);





        button_lock  = (ImageView)rootView.findViewById(R.id.button_lock);
        button_lock.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(lock == true){
                    Bitmap bm_lock = BitmapFactory.decodeResource(getResources(),R.drawable.unlock);
                    roundedImage_lock = new RoundImage(bm_lock);
                    button_lock.setImageDrawable(roundedImage_lock);
                    //call unlock function here
                    lock = false;
                    MainActivity2 a = (MainActivity2)getActivity();

                    if(a.mBluetoothSocket != null) {

                        try {
                            a.outStream = a.mBluetoothSocket.getOutputStream();
                        } catch (IOException e) {
                            Toast.makeText(a.getApplicationContext(), "Socket Error", Toast.LENGTH_SHORT).show();
                        }

                        String message = "L0#";
                        byte[] msgBuffer = message.getBytes();

                        try {
                            a.outStream.write(msgBuffer);
                        } catch (IOException e) {
                            Toast.makeText(a.getApplicationContext(), "Write Error", Toast.LENGTH_SHORT).show();
                        }
                    }

                }else{
                    Bitmap bm_lock = BitmapFactory.decodeResource(getResources(),R.drawable.lock);
                    roundedImage_lock = new RoundImage(bm_lock);
                    button_lock.setImageDrawable(roundedImage_lock);
                    button_lock.setTag(70);
                    lock = true;
                    MainActivity2 a = (MainActivity2)getActivity();

                    if(a.mBluetoothSocket != null) {

                        try {
                            a.outStream = a.mBluetoothSocket.getOutputStream();
                        } catch (IOException e) {
                            Toast.makeText(a.getApplicationContext(), "Socket Error", Toast.LENGTH_SHORT).show();
                        }

                        String message = "L1#";
                        byte[] msgBuffer = message.getBytes();

                        try {
                            a.outStream.write(msgBuffer);
                        } catch (IOException e) {
                            Toast.makeText(a.getApplicationContext(), "Write Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });

        //imageView.compress(Bitmap.CompressFormat.JPEG, 90, out);
        button_lock = (ImageView)
                rootView.findViewById(R.id.button_lock);
        Bitmap bm_lock = BitmapFactory.decodeResource(getResources(),R.drawable.lock);

        roundedImage_lock = new RoundImage(bm_lock);
        button_lock.setImageDrawable(roundedImage_lock);





        button_light = (ImageView)
                rootView.findViewById(R.id.button_light);
        Bitmap bm_light = BitmapFactory.decodeResource(getResources(),R.drawable.no_light);

        roundedImage_light = new RoundImage(bm_light);
        button_light.setImageDrawable(roundedImage_light);

        button_light.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(light == true){
                    Bitmap bm_light = BitmapFactory.decodeResource(getResources(),R.drawable.no_light);
                    roundedImage_light = new RoundImage(bm_light);
                    button_light.setImageDrawable(roundedImage_light);
                    //call unlock function here

                    light = false;
                    MainActivity2 a = (MainActivity2)getActivity();

                    if(a.mBluetoothSocket != null) {

                        try {
                            a.outStream = a.mBluetoothSocket.getOutputStream();
                        } catch (IOException e) {
                            Toast.makeText(a.getApplicationContext(), "Socket Error", Toast.LENGTH_SHORT).show();
                        }

                        String message = "F0#";
                        byte[] msgBuffer = message.getBytes();

                        try {
                            a.outStream.write(msgBuffer);
                        } catch (IOException e) {
                            Toast.makeText(a.getApplicationContext(), "Write Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Bitmap bm_light = BitmapFactory.decodeResource(getResources(),R.drawable.light);
                    roundedImage_light = new RoundImage(bm_light);
                    button_light.setImageDrawable(roundedImage_light);
                    button_light.setTag(70);

                    light = true;
                    MainActivity2 a = (MainActivity2)getActivity();

                    if(a.mBluetoothSocket != null) {

                        try {
                            a.outStream = a.mBluetoothSocket.getOutputStream();
                        } catch (IOException e) {
                            Toast.makeText(a.getApplicationContext(), "Socket Error", Toast.LENGTH_SHORT).show();
                        }

                        String message = "F1#";
                        byte[] msgBuffer = message.getBytes();

                        try {
                            a.outStream.write(msgBuffer);
                        } catch (IOException e) {
                            Toast.makeText(a.getApplicationContext(), "Write Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });


        changeDefaultBikeText.setOnClickListener(new OnClickListener(){

            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), ChangeBikesActivity.class), CHANGE_BIKE);
            }
        });

        imageView1.setOnClickListener(new View.OnClickListener() {

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
                        Intent intent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        startActivityForResult(
                                Intent.createChooser(intent, "Select File"),
                                SELECT_FILE);
//finish selecting file I guessss
                        TextView addbike = (TextView)rootView.findViewById(R.id.textView);
                        addbike.setVisibility(View.INVISIBLE);
                    }
                });


            }
            // Perform action on click
        });

        /*
        Button buttonFindBike = (Button) rootView.findViewById(R.id.button_find_bikes);
        buttonFindBike.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Perform action on click
                Toast.makeText(getActivity(), "Finding bikes...", Toast.LENGTH_SHORT).show();
            }
        });*/

        return rootView;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == SELECT_FILE){
                Uri selectedImage = data.getData();
                String[] projection = { MediaColumns.DATA };

                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        projection, null, null, null);
                cursor.moveToFirst();
                int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
                cursor.moveToFirst();

                String selectedImagePath = cursor.getString(column_index);

                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                roundedImage_def = new RoundImage(bm);
                imageView1.setScaleType(ScaleType.FIT_XY);
                imageView1.setImageDrawable(roundedImage_def);

            }
<<<<<<< HEAD
=======
            else if(requestCode == CHANGE_BIKE){
                activeBikeText.setText(data.getStringExtra("bike_name"));
                current_user.put("default_bike_id", data.getDoubleExtra("bike_id", 0));
                current_user.saveInBackground();

                ParseQuery<ParseObject> query = ParseQuery.getQuery("bike");
                query.whereEqualTo("bike_id", data.getDoubleExtra("bike_id", 0));

                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> postList, ParseException e) {
                        if (e == null && postList.size() > 0) {
                            postList.get(0).put("last_user",current_user.get("user_id"));
                        } else {
                            Log.d("MYTAG", "Post retrieval failed...");
                        }
                    }
                });
            }

>>>>>>> 601153cf374ce252455b8d3f54895e54e5048021
        }
    }

<<<<<<< HEAD
    //public void addBikeToParse(String bikename, String description, String bikeID) {
=======
>>>>>>> 601153cf374ce252455b8d3f54895e54e5048021
    public void addBikeToParse(String bikename, String description) {
        ParseUser current_user = ParseUser.getCurrentUser();

        Double bikeID = Math.random() * 1000000;

        // Get the user's old list of bikes
        ArrayList<Double> user_bikes = (ArrayList<Double>) current_user.get("bikes_used");
        // Add the new bike to the list
<<<<<<< HEAD
        user_bikes.add(bikeID);

=======
>>>>>>> 601153cf374ce252455b8d3f54895e54e5048021
        current_user.put("my_groups", user_bikes);
        current_user.saveInBackground();

        // Create a new bike object
        ParseObject new_bike = new ParseObject("bike");
<<<<<<< HEAD
=======

>>>>>>> 601153cf374ce252455b8d3f54895e54e5048021
        new_bike.put("bike_name", bikename);
        new_bike.put("bike_id", bikeID);
        ArrayList<Double> temp_bikes_owned = new ArrayList<Double>();
        temp_bikes_owned = (ArrayList<Double>) current_user.get("bikes_owned");
        temp_bikes_owned.add(bikeID); // random bike ID value
        current_user.put("bikes_owned", temp_bikes_owned);

     //new_bike.put("bike_id", bikeID);
        new_bike.put("bike_description", description);
        new_bike.put("owner_id", current_user.get("user_id"));
        new_bike.put("last_user", 0);
        new_bike.put("current_loc", new ParseGeoPoint(41.4242, 122.3844));
        new_bike.put("private_flag", "false");
        new_bike.put("locked_flag", "false");

        Log.d("MYTAG", "bike_id: " + bikeID);
//        ArrayList<String> temp_bikes_owned = new ArrayList<String>();
//        temp_bikes_owned = (ArrayList<String>) current_user.get("bikes_owned");
//        temp_bikes_owned.add(bikeID); // random bike ID value
//        current_user.put("bikes_owned", temp_bikes_owned);
//>>>>>>> 5fb9c98bc030b7d25e139f085e490766d03634cd
        count = count + 1;

        currentDefaultBikeId = current_user.getNumber("default_bike_id").doubleValue();
        if(currentDefaultBikeId == 0){
            current_user.put("default_bike_id", bikeID);
            new_bike.put("last_user", current_user.get("user_id"));
            activeBikeText.setText(bikename);
        }

        current_user.saveInBackground();
<<<<<<< HEAD
=======

>>>>>>> 601153cf374ce252455b8d3f54895e54e5048021

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
