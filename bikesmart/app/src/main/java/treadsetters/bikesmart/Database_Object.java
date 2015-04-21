package treadsetters.bikesmart;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

/**
 * Created by olivertownsend on 4/16/15.
 */
public class Database_Object {

    /*
    Class to add new user to BikeSmart database
     */
    public class BikeSmart_User {
        // bikes_owned
        // bikes_used
        // friends
        // groups
        // user_id
        String user_id;
        // notifications
        // messages
        // default_bike_id
        // first_name
        String first_name;
        // last_name
        String last_name;

        BikeSmart_User(String u_id, String f_name, String l_name) {

            this.user_id = u_id;
            this.first_name = f_name;
            this.last_name = l_name;
        }

        /*
        Stores local bike variables into a Parse object. Returns Parse Object
         */
        public ParseUser setUserData() {

            ParseUser current_user = ParseUser.getCurrentUser();
            current_user.put("user_id", this.user_id);
            current_user.put("first_name", this.first_name);
            current_user.put("last_name", this.last_name);

            return current_user;

        }


        public void saveUserToParse(ParseUser user) {
            // Save the post and return
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        // saved successfully
                    } else {
//                        Toast.makeText(BikesFragment.getActivity(),
//                                "Error saving: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }















    /*
    Class to add new bike to BikeSmart database
     */
    public class BikeSmart_Bike {
        // bike_name
        String bike_name;
        // owner_id
        String owner_id;
        // current_loc
        // bike_id
        String bike_id;
        // private_flag
        boolean private_flag;
        // locked
        boolean locked;
        // bike_description
        String bike_description;

        /*
        Bike constructor
         */
        BikeSmart_Bike(String b_name, String o_id,
                       boolean p_flag, boolean lock, String b_description) {

            this.bike_id = Integer.toString((int)Math.random() * 1000000);
            this.bike_name = b_name;
            this.owner_id = o_id;
            this.private_flag = p_flag;
            this.locked = lock;
            this.bike_description = b_description;
        }

        /*
        Stores local bike variables into a Parse object. Returns Parse Object
         */
        public ParseObject setBikeData() {

            ParseUser current_user = ParseUser.getCurrentUser();
            ParseObject new_bike = new ParseObject("bike");
            new_bike.put("bike_name", this.bike_name);
            new_bike.put("owner_id", this.owner_id);
            new_bike.put("bike_id", this.bike_id);
            new_bike.put("private_flag", this.private_flag);
            new_bike.put("locked", this.locked);
            new_bike.put("bike_description", this.bike_description);

            return new_bike;

        }

        /*
        Save bike object to database
         */
        public void saveBikeToParse(ParseObject new_bike) {


            // Save the post and return
            new_bike.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        // saved successfully
                    } else {
//                        Toast.makeText(BikesFragment.getActivity(),
//                                "Error saving: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }














    /*
    Class to add new group to BikeSmart database
     */
    public class BikeSmart_Group {
        // group_id
        String group_id;
        // group_name
        String group_name;
        // bikes
        // admin_id
        String admin_id;
        // members
        ArrayList<Double> members;

        BikeSmart_Group (String g_id, String g_name, String a_id) {
            this.group_id = g_id;
            this.group_name = g_name;
            this.admin_id = a_id;
        }


        /*
        Save group object to database
         */
        public void saveGroupToParse(ParseObject new_group) {

            // Save the post and return
            new_group.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        // saved successfully
                    } else {
//                        Toast.makeText(BikesFragment.getActivity(),
//                                "Error saving: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


        /*
        Adds the current user to group with group ID g_id
         */
        public void addUserToGroup (String g_id) {

            // add group id to current users list of groups
            ParseUser current_user = ParseUser.getCurrentUser();
            current_user.put("groups",g_id);

            // add user id to this groups members list
            ArrayList<Double> members_copy = new ArrayList<Double>();

//            ParseObject group = new ParseObject();
//            //group.get()
//
//            ParseQuery<ParseObject> query = ParseQuery.getQuery("Group");
//            query.whereEqualTo("group_id", g_id);
//            query.findInBackground(new FindCallback<ParseObject>() {
//                public void done(List<ParseObject> scoreList, ParseException e) {
//                    if (e == null) {
//                        Log.d("score", "Retrieved " + scoreList.size() + " scores");
//                    } else {
//                        Log.d("score", "Error: " + e.getMessage());
//                    }
//                }
//            });
//
//
//            // Retrieve the object by id
//            query.getInBackground("xWMyZ4YEGZ", new GetCallback<ParseObject>() {
//                public void done(ParseObject gameScore, ParseException e) {
//                    if (e == null) {
//                        // Now let's update it with some new data. In this case, only cheatMode and score
//                        // will get sent to the Parse Cloud. playerName hasn't changed.
//                        gameScore.put("score", 1338);
//                        gameScore.put("cheatMode", true);
//                        gameScore.saveInBackground();
//                    }
//                }
//            });
//
//
//
//            // -----------------------
//
//
//
//
//            ParseUser current_user = ParseUser.getCurrentUser();
//
//            members_copy.clear();
//            members_copy = (ArrayList<Double>) current_user.get("groups");
//
//            for (double bike_id : bikes_used_copy) {
//                Log.d("MYTAG", "ID is: " + bike_id);
//            }
//
//
//
//            ParseUser current_user = ParseUser.getCurrentUser();
//            ParseObject new_bike = new ParseObject("bike");
//            new_bike.put("bike_name", bikename);
//
//
//
//            Log.d("MYTAG", "bikeID: " + bikeID);
//            new_bike.put("bikeID", bikeID);
//            ArrayList<Double> temp_bikes_used = new ArrayList<Double>();
//            temp_bikes_used = (ArrayList<Double>) current_user.get("bikes_used");
//            temp_bikes_used.add(bikeID); // random bike ID value
//            current_user.put("bikes_used", temp_bikes_used);
//            count = count + 1;
//
//            // Save the post and return
//            new_bike.saveInBackground(new SaveCallback() {
//
//                @Override
//                public void done(ParseException e) {
//                    if (e == null) {
////                    setResult(RESULT_OK);
////                    finish();
//                        Toast.makeText(getActivity(), "Bike Successfully Added!", Toast.LENGTH_SHORT).show();
//
//                    } else {
//                        Toast.makeText(getActivity(),
//                                "Error saving: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//
//            });
        }
    }

}
