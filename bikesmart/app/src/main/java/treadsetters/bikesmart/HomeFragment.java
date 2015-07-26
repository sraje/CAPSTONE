package treadsetters.bikesmart;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
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
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


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
    private ParseFile photoFile;
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
    ParseObject currentDefaultBike;
    String      newBikeName;
    String      newBikeDescription;

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

        activeBikeText = (TextView) rootView.findViewById(R.id.active_bike_name);
        currentDefaultBikeId = current_user.getNumber("default_bike_id").doubleValue();
        if(currentDefaultBikeId != 0){
            ParseQuery<ParseObject> query = ParseQuery.getQuery("bike");
            query.whereEqualTo("bike_id", currentDefaultBikeId);

            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> postList, ParseException e) {
                    if (e == null && postList.size() > 0) {
                        currentDefaultBike = postList.get(0);
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

        button_locate.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Double bike_id = (Double) ParseUser.getCurrentUser().get("default_bike_id");

                FragmentManager fragmentManager = getFragmentManager(); // For AppCompat use getSupportFragmentManager
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                // Switch to the bike details fragment.
                Fragment fragment = fragmentManager.findFragmentByTag("bike_details");
                if (fragment == null) {
                    fragment = new BikeDetailsFragment();
                    transaction.add(R.id.container, fragment);
                } else {
                    transaction.show(fragment);
                }
                Bundle args = new Bundle();
                args.putDouble("bike_id", bike_id);
                fragment.setArguments(args);

                // Make sure the user can press 'back'
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        /*
        Set and save default bike
         */
        populateDefaultBike();




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

                    if(currentDefaultBike != null) {
                        currentDefaultBike.put("locked_flag", "false");
                        currentDefaultBike.saveInBackground();
                    }

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

                    if(currentDefaultBike != null) {
                        currentDefaultBike.put("locked_flag", "true");
                        currentDefaultBike.saveInBackground();
                    }

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

                Log.d("MYTAG", "ImageView1 onClick 1");

            }});



        imageView1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final EditText input = (EditText) rootView.findViewById(R.id.bike_name);

                Log.d("MYTAG", "ImageView1 onClick 2");
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

                        newBikeName = bikename;
                        newBikeDescription = description;
                        Log.d("MYTAG", "newbikename1, newbikedesc1: " + newBikeName + " " + newBikeDescription);
                        addBikeToParse(bikename, description);
                        populateDefaultBike();
                        refreshFrag();
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
//                        TextView addbike = (TextView)rootView.findViewById(R.id.textView);
//                        addbike.setVisibility(View.INVISIBLE);
                    }
                });


            }
            // Perform action on click
        });


        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == SELECT_FILE){
                Uri selectedImage = data.getData();
                try {
                    InputStream iStream = getActivity().getContentResolver().openInputStream(selectedImage);
                    byte[] bikePhotoData = getBytes(iStream);
                } catch (IOException e) {
                    Log.d("MYTAG", "Failed to save photo.");
                    e.printStackTrace();
                }

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
//                imageView1.setImageDrawable(roundedImage_def);
//                imageView1.setImageDrawable(roundedImage_def);

                Log.d("MYTAG", "newbikename, newbikedesc: " + newBikeName + " " + newBikeDescription);

            }
            else if(requestCode == CHANGE_BIKE){
                activeBikeText.setText(data.getStringExtra("bike_name"));
                current_user.put("default_bike_id", data.getDoubleExtra("bike_id", 0));
                current_user.saveInBackground();

                ParseQuery<ParseObject> query = ParseQuery.getQuery("bike");
                double changeBikeID = data.getDoubleExtra("bike_id", 0);
                query.whereEqualTo("bike_id", changeBikeID);
                current_user.put("default_bike_id", changeBikeID);
                populateDefaultBike();
                refreshFrag();

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

        }
    }


    public void populateDefaultBike() {
        ParseUser current_user = ParseUser.getCurrentUser();

        /*
        BIKE NAME
         */
        currentDefaultBikeId = current_user.getNumber("default_bike_id").doubleValue();
        double default_id = currentDefaultBikeId;
        Log.d("MYTAG", "Default bike id is " + default_id);


        if(default_id > -1) {
            // Default bike has been set and a photo has been uploaded.

            ParseQuery<ParseObject> query = ParseQuery.getQuery("bike");
            Log.d("MYTAG", "Setting default bike to " + default_id);
            query.whereEqualTo("bike_id", default_id);

            /*
            Query to find this user's default bike id.
             */
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> postList, ParseException e) {
                    if (e != null) {
                        Log.d("MYTAG", "e != null");
                    }
                    if (!(postList.size() > 0)) {
                        Log.d("MYTAG", "!postList.size() > 0");
                    }
                    if (e == null && postList.size() > 0) {

                        /*
                        Set default bike text.
                         */
                        String default_name = postList.get(0).getString("bike_name").toString();
                        String default_desc = postList.get(0).getString("bike_description").toString();
                        Log.d("MYTAG", "Setting default bikename to " + default_name);
                        setDefaultBikeText(default_name, default_desc);

                        /*
                        Set default bike pic.
                         */
                        Log.d("MYTAG", "Getting default bike photo in populateDefaultBike");
                        ParseFile pic = (ParseFile) postList.get(0).get("bike_photo");
                        byte[] bitmapdata = new byte[0];
                        try {
                            bitmapdata = pic.getData();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }


                        Bitmap bm;
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                        final int REQUIRED_SIZE = 200;
                        int scale = 1;
                        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                            scale *= 2;
                        options.inSampleSize = scale;
                        options.inJustDecodeBounds = false;
                        bm = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

                        roundedImage_def = new RoundImage(bm);
                        imageView1.setScaleType(ScaleType.FIT_XY);
                        imageView1.setImageDrawable(roundedImage_def);


                        /*
                        Set default bike location.
                         */
                        ParseGeoPoint myloc = (ParseGeoPoint) postList.get(0).get("current_loc");
                        setLocationText(myloc);

                    } else {
                        Log.d("MYTAG", "Default bike name/photo retrieval failed...");
                        if (e != null)
                            e.printStackTrace();
                    }
                }
            });
        }




    }

    public void refreshFrag() {
        // Reload current fragment
        FragmentManager fragmentManager = getFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentByTag("home");
        if(currentFragment != null){
            //if the other fragment is visible, hide it.
//            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("notifications")).commit();
            FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
            fragTransaction.detach(currentFragment);
            fragTransaction.attach(currentFragment);
            fragTransaction.commit();
        }
    }


    public void setLocationText(ParseGeoPoint loc) {

        Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0)
            System.out.println(addresses.get(0).getLocality());

        TextView lastLocationTextView = (TextView) getActivity().findViewById(R.id.last_location);
        Log.d("MYTAG", "Setting location to " + addresses.get(0).getLocality());
        lastLocationTextView.setText(addresses.get(0).getLocality());
    }


    public void setDefaultBikeText(String name, String desc) {
        activeBikeText.setText(name);
    }






    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    public void saveDefaultBikePhoto(byte[] data) {

        // Resize photo from camera byte array
        Bitmap bikeImage = BitmapFactory.decodeByteArray(data, 0, data.length);
        Bitmap bikeImageScaled = Bitmap.createScaledBitmap(bikeImage, 200, 200
                * bikeImage.getHeight() / bikeImage.getWidth(), false);

        // Override Android default landscape orientation and save portrait
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotatedScaledMealImage = Bitmap.createBitmap(bikeImageScaled, 0,
                0, bikeImageScaled.getWidth(), bikeImageScaled.getHeight(),
                matrix, true);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        rotatedScaledMealImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);

        byte[] scaledData = bos.toByteArray();

        photoFile = new ParseFile("default_bike_photo.jpg", scaledData);
        photoFile.saveInBackground(new SaveCallback() {

            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(getActivity(),
                            "Error saving: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                } else {
                    ParseUser.getCurrentUser().put("default_bike_photo", photoFile);
                }
            }
        });
    }


    public void setDefaultBike(double bike_id) {

        ParseUser current_user = ParseUser.getCurrentUser();
        current_user.put("default_bike_id", bike_id);

    }


    public void addBikeToParse(String bikename, String description) {
        ParseUser current_user = ParseUser.getCurrentUser();
        // Get the user's old list of bikes
        ArrayList<Double> user_bikes = (ArrayList<Double>) current_user.get("bikes_used");
        // Add the new bike to the list
        current_user.put("my_groups", user_bikes);
        current_user.saveInBackground();

        // Create a new bike object
        ParseObject new_bike = new ParseObject("bike");

        double bikeID = Math.random() * 1000000;


        Log.d("MYTAG", "bikeID: " + bikeID);
        ArrayList<Double> temp_bikes_owned = new ArrayList<Double>();
        temp_bikes_owned = (ArrayList<Double>) current_user.get("bikes_owned");
        temp_bikes_owned.add(bikeID); // random bike ID value
        current_user.put("bikes_owned", temp_bikes_owned);

        Bitmap bmp = roundedImage_def.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        ParseFile roundBikeImage = new ParseFile("roundBikeImage.jpg", byteArray);

        new_bike.put("bike_name", bikename);
        new_bike.put("bike_id", bikeID);
        new_bike.put("bike_description", description);
        new_bike.put("owner_id", current_user.get("user_id"));
        new_bike.put("bike_owner_string", current_user.get("username"));
        new_bike.put("last_user", 0);
        new_bike.put("current_loc", new ParseGeoPoint(34.413329, -119.860972));
        new_bike.put("private_flag", "false");
        new_bike.put("locked_flag", "true");
        new_bike.put("bike_photo", roundBikeImage);
        new_bike.put("dist_traveled", 0);



        Log.d("MYTAG", "bike_id: " + bikeID);

        currentDefaultBikeId = current_user.getNumber("default_bike_id").doubleValue();

        if(currentDefaultBikeId == 0){
            Log.d("MYTAG", "currentDefaultBikeId == 0");
            current_user.put("default_bike_id", bikeID);
            new_bike.put("last_user", current_user.get("user_id"));
            activeBikeText.setText(bikename);
            setLocationText(new ParseGeoPoint(34.413329, -119.860972));
            imageView1.setScaleType(ScaleType.FIT_XY);
            imageView1.setImageDrawable(roundedImage_def);
            currentDefaultBike = new_bike;
        }

        current_user.saveInBackground();


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


        populateDefaultBike();
        refreshFrag();

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