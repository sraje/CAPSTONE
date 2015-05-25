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
import android.graphics.Matrix;
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
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public void populateActiveBike() {
        ParseUser current_user = ParseUser.getCurrentUser();

        double active_id = (double) current_user.get("active_bike");
        Log.d("MYTAG", "Active bike id is " + active_id);


        if(active_id != -1) {
            // Active bike has been set and a photo has been uploaded.

            boolean stop = false;

            ArrayList<Double> bikes_owned_copy = new ArrayList<Double>();
            ArrayList<String> bikes_used_copy = new ArrayList<String>();

            bikes_owned_copy = (ArrayList<Double>) current_user.get("bikes_owned");
            bikes_used_copy = (ArrayList<String>) current_user.get("bike_used");


            ParseQuery<ParseObject> query = ParseQuery.getQuery("bike");
            Log.d("MYTAG", "Setting active bike to " + active_id);
            query.whereEqualTo("bike_id", active_id);

            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> postList, ParseException e) {
                    if (e == null && postList.size() > 0) {
                        Log.d("MYTAG", "postList size: " + postList.size());
                        String active_name = postList.get(0).getString("bike_name").toString();
                        setActiveText(active_name);
                        Log.d("MYTAG", "Active bike is " + active_name);

                        // TODO: Actually save bike pictures to Parse
                        postList.get(0).get("bike_picture");

                    } else {
                        Log.d("MYTAG", "Post retrieval failed...");
                    }
                }
            });

            // ParseUser.getCurrentUser().put("active_bike_photo", photoFile);
            ParseFile photoFile = (ParseFile) current_user.get("active_bike_photo");
            Uri imageUri = Uri.parse(photoFile.getUrl());
            setActiveBikePhoto(imageUri);
        }



    }

    public void setActiveBikePhoto(Uri image) {
        String[] projection = { MediaColumns.DATA };

        Cursor cursor = getActivity().getContentResolver().query(image,
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

    public void setActiveText(String name) {
        TextView activeNameTextView = (TextView) getActivity().findViewById(R.id.add_bike_textview);
        activeNameTextView.setText(name);
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


        /*
        TODO: Set and save active bike
        I'm thinkin ...somethin like...saving an attribute to
        this current parse user called "active_bike" or sumthin..
        then like...totally just storing a bike_id in there. and
        then like...if there's no active bike, store -1 in there
        or sumthin...then just run a query here in onCreate, get
        that value, check if -1, otherwise, query for that bike_id,
        populate dis business....will be super chill.
         */
        populateActiveBike();


        /*
        TODO: Set last seen location.
         */
        TextView lastLocationTextView = (TextView)rootView.findViewById(R.id.last_location);
        lastLocationTextView.setText("in your butt");



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


        imageView1.setOnClickListener(new OnClickListener(){

            public void onClick(View view) {

                Log.d("MYTAG", "ImageView1 onClick 1");

            }});

        changeDefaultBikeText.setOnClickListener(new OnClickListener(){

            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), ChangeBikesActivity.class), CHANGE_BIKE);
            }
        });

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
//                        TextView addbike = (TextView)rootView.findViewById(R.id.textView);
//                        addbike.setVisibility(View.INVISIBLE);
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
                try {
                    InputStream iStream = getActivity().getContentResolver().openInputStream(selectedImage);
                    byte[] bikePhotoData = getBytes(iStream);
                    saveActiveBikePhoto(bikePhotoData);
                } catch (IOException e) {
                    Log.d("MYTAG", "Failed to save photo.");
                    e.printStackTrace();
                }

                setActiveBikePhoto(selectedImage);

            }
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

        }

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


    public void saveActiveBikePhoto(byte[] data) {

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

        photoFile = new ParseFile("bike_photo.jpg", scaledData);
        photoFile.saveInBackground(new SaveCallback() {

            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(getActivity(),
                            "Error saving: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                } else {
                    ParseUser.getCurrentUser().put("active_bike_photo", photoFile);
                }
            }
        });
    }


    public void setActiveBike(double bike_id) {

        ParseUser current_user = ParseUser.getCurrentUser();
        current_user.put("active_bike", bike_id);

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
        new_bike.put("last_user", 0);
        new_bike.put("current_loc", "");
        new_bike.put("private_flag", "false");
        new_bike.put("locked_flag", "false");



        Log.d("MYTAG", "bike_id: " + bikeID);
        count = count + 1;

        currentDefaultBikeId = current_user.getNumber("default_bike_id").doubleValue();
        if(currentDefaultBikeId == 0){
            current_user.put("default_bike_id", bikeID);
            new_bike.put("last_user", current_user.get("user_id"));
            activeBikeText.setText(bikename);
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

        setActiveBike(bikeID);
        populateActiveBike();


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
