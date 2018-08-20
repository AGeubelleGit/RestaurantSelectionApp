package edu.illinois.finalproject;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import edu.illinois.finalproject.ZomatoAPIFiles.RestaurantContainer;
import edu.illinois.finalproject.ZomatoAPIFiles.RestaurantFirebaseContainer;
import edu.illinois.finalproject.ZomatoAPIFiles.ZomatoSearchResult;
import edu.illinois.finalproject.grouplist.GroupInfo;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("edu.illinois.finalproject", appContext.getPackageName());
    }

    @Test
    public void PutYourFirebaseTestsHere() throws Exception {
        //Access hard coded json and add all restaurants to the database.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference rootReference = database.getReference("Group1");

        Gson gson = new Gson();
        ZomatoSearchResult result = gson.fromJson(restaurants_json, ZomatoSearchResult.class);

        for(RestaurantContainer restaurant: result.getRestaurants()){
            //Set the name to the unique restaurant id
            String restaurantId = Integer.toString(restaurant.getRestaurant().getId());
            rootReference.child(restaurantId).setValue(restaurant);
        }

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await(5, TimeUnit.SECONDS);
    }

    @Test
    public void writeGroups() throws Exception {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference rootReference = database.getReference("");
        final DatabaseReference groupsReference = database.getReference("Groups");

        String[] names = {"A Group", "Sunday Dinner", "Hi..."};

        for (String newName: names) {

            String newRootKey = rootReference.push().getKey();

            DatabaseReference newGroupRef = groupsReference.push().getRef();
            newGroupRef.child("Name").setValue(newName);
            newGroupRef.child("Password").setValue("password");
            newGroupRef.child("Creator").setValue("test");
            newGroupRef.child("Id").setValue(newRootKey);
            newGroupRef.child("Key").setValue(newGroupRef.getKey());

            rootReference.child(newRootKey).setValue("No Restaurants");
        }
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await(30, TimeUnit.SECONDS);

    }

    @Test
    public void removeGroups() throws Exception {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference rootReference = database.getReference("");
        final DatabaseReference groupsReference = database.getReference("Groups");

        //Get children from databse: https://stackoverflow.com/questions/32886546/how-to-get-all-child-list-from-firebase-android
        Log.d("TEST", "ADD LISTENER");
        groupsReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if(snapshot == null) {
                    Log.d("TEST", "NULL SNAPSHOT");
                }else {
                    Map<String, HashMap<String, String>> td =  (HashMap<String, HashMap<String, String>>) snapshot.getValue();
                    Collection<HashMap<String, String>> values = td.values();
                    Iterator<HashMap<String, String>> iter = values.iterator();
                    while(iter.hasNext()) {
                        HashMap<String,String> group = iter.next();

                        String groupName = group.get("Name");
                        String groupId = group.get("Id");
                        String groupKey = group.get("Key");
                        if (groupName.toLowerCase().contains("group")) {
                            Log.d("TEST", groupName + " " + groupId + " " + groupKey);
                            groupsReference.child(groupKey).setValue(null);
                            rootReference.child(groupId).setValue(null);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Do nothing.
            }
        });

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await(10, TimeUnit.SECONDS);

    }

    @Test
    public void TestLikesAndDislikes() throws Exception {
        //Access hard coded json and add all restaurants to the database.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference rootReference = database.getReference("Test");

        Gson gson = new Gson();
        ZomatoSearchResult result = gson.fromJson(restaurants_json, ZomatoSearchResult.class);

        RestaurantContainer restaurant = result.getRestaurants()[0];
        String restaurantId = Integer.toString(restaurant.getRestaurant().getId());
        RestaurantFirebaseContainer restaurantFirebaseContainer = new RestaurantFirebaseContainer(restaurant.getRestaurant());
        restaurantFirebaseContainer.like("Alexandre");
        restaurantFirebaseContainer.dislike("OtherPerson");
        rootReference.child(restaurantId).setValue(restaurantFirebaseContainer);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await(5, TimeUnit.SECONDS);
    }

    /**
     * Removes the first restaurant from the database by accessing its id and setting its value to null
     * @throws Exception
     */
    @Test
    public void FirebaseRemoveTest() throws Exception {
        // They need to be in the Android tests file so that they can use Android services
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference rootReference = database.getReference("Group1");

        Gson gson = new Gson();
        ZomatoSearchResult result = gson.fromJson(restaurants_json, ZomatoSearchResult.class);

        RestaurantContainer toRemove = result.getRestaurants()[0];
        String restaurantId = Integer.toString(toRemove.getRestaurant().getId());

        DatabaseReference removeReference = rootReference.child(restaurantId);
        removeReference.setValue(null);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await(5, TimeUnit.SECONDS);
    }

    private final String restaurants_json = "{\n" +
            "  \"results_found\": 40507,\n" +
            "  \"results_start\": 0,\n" +
            "  \"results_shown\": 20,\n" +
            "  \"restaurants\": [\n" +
            "    {\n" +
            "      \"restaurant\": {\n" +
            "        \"R\": {\n" +
            "          \"res_id\": 16769241\n" +
            "        },\n" +
            "        \"apikey\": \"ff9233307cb49a3287dcdc2c8daed54c\",\n" +
            "        \"id\": \"16769241\",\n" +
            "        \"name\": \"Junior's\",\n" +
            "        \"url\": \"https://www.zomato.com/new-york-city/juniors-restaurant-theater-district?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"location\": {\n" +
            "          \"address\": \"1515 Broadway, New York 10019\",\n" +
            "          \"locality\": \"Theater District\",\n" +
            "          \"city\": \"New York City\",\n" +
            "          \"city_id\": 280,\n" +
            "          \"latitude\": \"40.7582472222\",\n" +
            "          \"longitude\": \"-73.9864416667\",\n" +
            "          \"zipcode\": \"10019\",\n" +
            "          \"country_id\": 216,\n" +
            "          \"locality_verbose\": \"Theater District\"\n" +
            "        },\n" +
            "        \"switch_to_order_menu\": 0,\n" +
            "        \"cuisines\": \"American, Desserts\",\n" +
            "        \"average_cost_for_two\": 45,\n" +
            "        \"price_range\": 3,\n" +
            "        \"currency\": \"$\",\n" +
            "        \"offers\": [],\n" +
            "        \"thumb\": \"https://b.zmtcdn.com/data/res_imagery/16769241_RESTAURANT_9441b6ae1f582f44f29a61d11052d476_c.png?fit=around%7C200%3A200&crop=200%3A200%3B%2A%2C%2A\",\n" +
            "        \"user_rating\": {\n" +
            "          \"aggregate_rating\": \"4.4\",\n" +
            "          \"rating_text\": \"Very Good\",\n" +
            "          \"rating_color\": \"5BA829\",\n" +
            "          \"votes\": \"777\"\n" +
            "        },\n" +
            "        \"photos_url\": \"https://www.zomato.com/new-york-city/juniors-restaurant-theater-district/photos?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1#tabtop\",\n" +
            "        \"menu_url\": \"https://www.zomato.com/new-york-city/juniors-restaurant-theater-district/menu?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1&openSwipeBox=menu&showMinimal=1#tabtop\",\n" +
            "        \"featured_image\": \"https://b.zmtcdn.com/data/res_imagery/16769241_RESTAURANT_9441b6ae1f582f44f29a61d11052d476_c.png\",\n" +
            "        \"has_online_delivery\": 0,\n" +
            "        \"is_delivering_now\": 0,\n" +
            "        \"deeplink\": \"zomato://restaurant/16769241\",\n" +
            "        \"has_table_booking\": 0,\n" +
            "        \"events_url\": \"https://www.zomato.com/new-york-city/juniors-restaurant-theater-district/events#tabtop?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"establishment_types\": []\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"restaurant\": {\n" +
            "        \"R\": {\n" +
            "          \"res_id\": 16769546\n" +
            "        },\n" +
            "        \"apikey\": \"ff9233307cb49a3287dcdc2c8daed54c\",\n" +
            "        \"id\": \"16769546\",\n" +
            "        \"name\": \"Katz's Delicatessen\",\n" +
            "        \"url\": \"https://www.zomato.com/new-york-city/katzs-delicatessen-lower-east-side?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"location\": {\n" +
            "          \"address\": \"205 East Houston Street, New York 10002\",\n" +
            "          \"locality\": \"Lower East Side\",\n" +
            "          \"city\": \"New York City\",\n" +
            "          \"city_id\": 280,\n" +
            "          \"latitude\": \"40.7223277778\",\n" +
            "          \"longitude\": \"-73.9873500000\",\n" +
            "          \"zipcode\": \"10002\",\n" +
            "          \"country_id\": 216,\n" +
            "          \"locality_verbose\": \"Lower East Side\"\n" +
            "        },\n" +
            "        \"switch_to_order_menu\": 0,\n" +
            "        \"cuisines\": \"Sandwich\",\n" +
            "        \"average_cost_for_two\": 30,\n" +
            "        \"price_range\": 2,\n" +
            "        \"currency\": \"$\",\n" +
            "        \"offers\": [],\n" +
            "        \"thumb\": \"https://b.zmtcdn.com/data/res_imagery/16769546_RESTAURANT_2282b97610391948c11d1e6bd5057b04_c.jpg?fit=around%7C200%3A200&crop=200%3A200%3B%2A%2C%2A\",\n" +
            "        \"user_rating\": {\n" +
            "          \"aggregate_rating\": \"4.9\",\n" +
            "          \"rating_text\": \"Excellent\",\n" +
            "          \"rating_color\": \"3F7E00\",\n" +
            "          \"votes\": \"4325\"\n" +
            "        },\n" +
            "        \"photos_url\": \"https://www.zomato.com/new-york-city/katzs-delicatessen-lower-east-side/photos?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1#tabtop\",\n" +
            "        \"menu_url\": \"https://www.zomato.com/new-york-city/katzs-delicatessen-lower-east-side/menu?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1&openSwipeBox=menu&showMinimal=1#tabtop\",\n" +
            "        \"featured_image\": \"https://b.zmtcdn.com/data/res_imagery/16769546_RESTAURANT_2282b97610391948c11d1e6bd5057b04_c.jpg\",\n" +
            "        \"has_online_delivery\": 0,\n" +
            "        \"is_delivering_now\": 0,\n" +
            "        \"deeplink\": \"zomato://restaurant/16769546\",\n" +
            "        \"has_table_booking\": 0,\n" +
            "        \"events_url\": \"https://www.zomato.com/new-york-city/katzs-delicatessen-lower-east-side/events#tabtop?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"establishment_types\": []\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"restaurant\": {\n" +
            "        \"R\": {\n" +
            "          \"res_id\": 16777384\n" +
            "        },\n" +
            "        \"apikey\": \"ff9233307cb49a3287dcdc2c8daed54c\",\n" +
            "        \"id\": \"16777384\",\n" +
            "        \"name\": \"Shake Shack\",\n" +
            "        \"url\": \"https://www.zomato.com/new-york-city/shake-shack-gramercy-flatiron?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"location\": {\n" +
            "          \"address\": \"Madison Square Park, 23rd & Madison, New York 10010\",\n" +
            "          \"locality\": \"Gramercy-Flatiron\",\n" +
            "          \"city\": \"New York City\",\n" +
            "          \"city_id\": 280,\n" +
            "          \"latitude\": \"40.7408681000\",\n" +
            "          \"longitude\": \"-73.9879841000\",\n" +
            "          \"zipcode\": \"10010\",\n" +
            "          \"country_id\": 216,\n" +
            "          \"locality_verbose\": \"Gramercy-Flatiron\"\n" +
            "        },\n" +
            "        \"switch_to_order_menu\": 0,\n" +
            "        \"cuisines\": \"American, Burger\",\n" +
            "        \"average_cost_for_two\": 30,\n" +
            "        \"price_range\": 2,\n" +
            "        \"currency\": \"$\",\n" +
            "        \"offers\": [],\n" +
            "        \"thumb\": \"https://b.zmtcdn.com/data/res_imagery/16777384_CHAIN_10d8440cfa6b530875c2cc6067e31349_c.jpg?fit=around%7C200%3A200&crop=200%3A200%3B%2A%2C%2A\",\n" +
            "        \"user_rating\": {\n" +
            "          \"aggregate_rating\": \"4.9\",\n" +
            "          \"rating_text\": \"Excellent\",\n" +
            "          \"rating_color\": \"3F7E00\",\n" +
            "          \"votes\": \"3389\"\n" +
            "        },\n" +
            "        \"photos_url\": \"https://www.zomato.com/new-york-city/shake-shack-gramercy-flatiron/photos?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1#tabtop\",\n" +
            "        \"menu_url\": \"https://www.zomato.com/new-york-city/shake-shack-gramercy-flatiron/menu?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1&openSwipeBox=menu&showMinimal=1#tabtop\",\n" +
            "        \"featured_image\": \"https://b.zmtcdn.com/data/res_imagery/16777384_CHAIN_10d8440cfa6b530875c2cc6067e31349_c.jpg\",\n" +
            "        \"has_online_delivery\": 0,\n" +
            "        \"is_delivering_now\": 0,\n" +
            "        \"deeplink\": \"zomato://restaurant/16777384\",\n" +
            "        \"has_table_booking\": 0,\n" +
            "        \"events_url\": \"https://www.zomato.com/new-york-city/shake-shack-gramercy-flatiron/events#tabtop?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"establishment_types\": []\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"restaurant\": {\n" +
            "        \"R\": {\n" +
            "          \"res_id\": 16771928\n" +
            "        },\n" +
            "        \"apikey\": \"ff9233307cb49a3287dcdc2c8daed54c\",\n" +
            "        \"id\": \"16771928\",\n" +
            "        \"name\": \"Max Brenner\",\n" +
            "        \"url\": \"https://www.zomato.com/new-york-city/max-brenner-greenwich-village?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"location\": {\n" +
            "          \"address\": \"841 Broadway, New York 10003\",\n" +
            "          \"locality\": \"Greenwich Village\",\n" +
            "          \"city\": \"New York City\",\n" +
            "          \"city_id\": 280,\n" +
            "          \"latitude\": \"40.7344138889\",\n" +
            "          \"longitude\": \"-73.9914333333\",\n" +
            "          \"zipcode\": \"10003\",\n" +
            "          \"country_id\": 216,\n" +
            "          \"locality_verbose\": \"Greenwich Village\"\n" +
            "        },\n" +
            "        \"switch_to_order_menu\": 0,\n" +
            "        \"cuisines\": \"New American, Desserts\",\n" +
            "        \"average_cost_for_two\": 70,\n" +
            "        \"price_range\": 4,\n" +
            "        \"currency\": \"$\",\n" +
            "        \"offers\": [],\n" +
            "        \"thumb\": \"https://b.zmtcdn.com/data/res_imagery/16771928_RESTAURANT_7d9c8d906a8ab2130214b424f415e514_c.jpg?fit=around%7C200%3A200&crop=200%3A200%3B%2A%2C%2A\",\n" +
            "        \"user_rating\": {\n" +
            "          \"aggregate_rating\": \"4.6\",\n" +
            "          \"rating_text\": \"Excellent\",\n" +
            "          \"rating_color\": \"3F7E00\",\n" +
            "          \"votes\": \"1067\"\n" +
            "        },\n" +
            "        \"photos_url\": \"https://www.zomato.com/new-york-city/max-brenner-greenwich-village/photos?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1#tabtop\",\n" +
            "        \"menu_url\": \"https://www.zomato.com/new-york-city/max-brenner-greenwich-village/menu?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1&openSwipeBox=menu&showMinimal=1#tabtop\",\n" +
            "        \"featured_image\": \"https://b.zmtcdn.com/data/res_imagery/16771928_RESTAURANT_7d9c8d906a8ab2130214b424f415e514_c.jpg\",\n" +
            "        \"has_online_delivery\": 0,\n" +
            "        \"is_delivering_now\": 0,\n" +
            "        \"deeplink\": \"zomato://restaurant/16771928\",\n" +
            "        \"has_table_booking\": 0,\n" +
            "        \"events_url\": \"https://www.zomato.com/new-york-city/max-brenner-greenwich-village/events#tabtop?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"establishment_types\": []\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"restaurant\": {\n" +
            "        \"R\": {\n" +
            "          \"res_id\": 16761344\n" +
            "        },\n" +
            "        \"apikey\": \"ff9233307cb49a3287dcdc2c8daed54c\",\n" +
            "        \"id\": \"16761344\",\n" +
            "        \"name\": \"Buddakan\",\n" +
            "        \"url\": \"https://www.zomato.com/new-york-city/buddakan-greenwich-village?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"location\": {\n" +
            "          \"address\": \"75 9th Avenue, New York 10011\",\n" +
            "          \"locality\": \"9th Avenue, Greenwich Village\",\n" +
            "          \"city\": \"New York City\",\n" +
            "          \"city_id\": 280,\n" +
            "          \"latitude\": \"40.7422320000\",\n" +
            "          \"longitude\": \"-74.0055112000\",\n" +
            "          \"zipcode\": \"10011\",\n" +
            "          \"country_id\": 216,\n" +
            "          \"locality_verbose\": \"9th Avenue, Greenwich Village, New York City\"\n" +
            "        },\n" +
            "        \"switch_to_order_menu\": 0,\n" +
            "        \"cuisines\": \"Chinese, Fusion, Asian\",\n" +
            "        \"average_cost_for_two\": 125,\n" +
            "        \"price_range\": 4,\n" +
            "        \"currency\": \"$\",\n" +
            "        \"offers\": [],\n" +
            "        \"thumb\": \"https://b.zmtcdn.com/data/res_imagery/16761344_RESTAURANT_09252a25cb35a75a4cb8dde2eef68870.jpg?fit=around%7C200%3A200&crop=200%3A200%3B%2A%2C%2A\",\n" +
            "        \"user_rating\": {\n" +
            "          \"aggregate_rating\": \"4.8\",\n" +
            "          \"rating_text\": \"Excellent\",\n" +
            "          \"rating_color\": \"3F7E00\",\n" +
            "          \"votes\": \"1525\"\n" +
            "        },\n" +
            "        \"photos_url\": \"https://www.zomato.com/new-york-city/buddakan-greenwich-village/photos?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1#tabtop\",\n" +
            "        \"menu_url\": \"https://www.zomato.com/new-york-city/buddakan-greenwich-village/menu?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1&openSwipeBox=menu&showMinimal=1#tabtop\",\n" +
            "        \"featured_image\": \"https://b.zmtcdn.com/data/res_imagery/16761344_RESTAURANT_09252a25cb35a75a4cb8dde2eef68870.jpg\",\n" +
            "        \"has_online_delivery\": 0,\n" +
            "        \"is_delivering_now\": 0,\n" +
            "        \"deeplink\": \"zomato://restaurant/16761344\",\n" +
            "        \"has_table_booking\": 0,\n" +
            "        \"events_url\": \"https://www.zomato.com/new-york-city/buddakan-greenwich-village/events#tabtop?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"establishment_types\": []\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"restaurant\": {\n" +
            "        \"R\": {\n" +
            "          \"res_id\": 16777320\n" +
            "        },\n" +
            "        \"apikey\": \"ff9233307cb49a3287dcdc2c8daed54c\",\n" +
            "        \"id\": \"16777320\",\n" +
            "        \"name\": \"Serendipity 3\",\n" +
            "        \"url\": \"https://www.zomato.com/new-york-city/serendipity-3-upper-east-side?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"location\": {\n" +
            "          \"address\": \"225 E 60th Street, New York 10022\",\n" +
            "          \"locality\": \"Upper East Side\",\n" +
            "          \"city\": \"New York City\",\n" +
            "          \"city_id\": 280,\n" +
            "          \"latitude\": \"40.7618990000\",\n" +
            "          \"longitude\": \"-73.9648580000\",\n" +
            "          \"zipcode\": \"10022\",\n" +
            "          \"country_id\": 216,\n" +
            "          \"locality_verbose\": \"Upper East Side\"\n" +
            "        },\n" +
            "        \"switch_to_order_menu\": 0,\n" +
            "        \"cuisines\": \"American, Desserts\",\n" +
            "        \"average_cost_for_two\": 75,\n" +
            "        \"price_range\": 4,\n" +
            "        \"currency\": \"$\",\n" +
            "        \"offers\": [],\n" +
            "        \"thumb\": \"https://b.zmtcdn.com/data/res_imagery/16777320_RESTAURANT_5bc92fcf35438e2404eb918270b2ea2c.jpg?fit=around%7C200%3A200&crop=200%3A200%3B%2A%2C%2A\",\n" +
            "        \"user_rating\": {\n" +
            "          \"aggregate_rating\": \"4.3\",\n" +
            "          \"rating_text\": \"Very Good\",\n" +
            "          \"rating_color\": \"5BA829\",\n" +
            "          \"votes\": \"1710\"\n" +
            "        },\n" +
            "        \"photos_url\": \"https://www.zomato.com/new-york-city/serendipity-3-upper-east-side/photos?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1#tabtop\",\n" +
            "        \"menu_url\": \"https://www.zomato.com/new-york-city/serendipity-3-upper-east-side/menu?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1&openSwipeBox=menu&showMinimal=1#tabtop\",\n" +
            "        \"featured_image\": \"https://b.zmtcdn.com/data/res_imagery/16777320_RESTAURANT_5bc92fcf35438e2404eb918270b2ea2c.jpg\",\n" +
            "        \"has_online_delivery\": 0,\n" +
            "        \"is_delivering_now\": 0,\n" +
            "        \"deeplink\": \"zomato://restaurant/16777320\",\n" +
            "        \"has_table_booking\": 0,\n" +
            "        \"events_url\": \"https://www.zomato.com/new-york-city/serendipity-3-upper-east-side/events#tabtop?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"establishment_types\": []\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"restaurant\": {\n" +
            "        \"R\": {\n" +
            "          \"res_id\": 16783153\n" +
            "        },\n" +
            "        \"apikey\": \"ff9233307cb49a3287dcdc2c8daed54c\",\n" +
            "        \"id\": \"16783153\",\n" +
            "        \"name\": \"Shake Shack\",\n" +
            "        \"url\": \"https://www.zomato.com/new-york-city/shake-shack-upper-west-side?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"location\": {\n" +
            "          \"address\": \"366 Columbus Avenue W 77th Street, New York 10024\",\n" +
            "          \"locality\": \"Upper West Side\",\n" +
            "          \"city\": \"New York City\",\n" +
            "          \"city_id\": 280,\n" +
            "          \"latitude\": \"40.7807222222\",\n" +
            "          \"longitude\": \"-73.9764944444\",\n" +
            "          \"zipcode\": \"10024\",\n" +
            "          \"country_id\": 216,\n" +
            "          \"locality_verbose\": \"Upper West Side\"\n" +
            "        },\n" +
            "        \"switch_to_order_menu\": 0,\n" +
            "        \"cuisines\": \"American, Burger\",\n" +
            "        \"average_cost_for_two\": 30,\n" +
            "        \"price_range\": 2,\n" +
            "        \"currency\": \"$\",\n" +
            "        \"offers\": [],\n" +
            "        \"thumb\": \"https://b.zmtcdn.com/data/res_imagery/16777384_CHAIN_10d8440cfa6b530875c2cc6067e31349_c.jpg?fit=around%7C200%3A200&crop=200%3A200%3B%2A%2C%2A\",\n" +
            "        \"user_rating\": {\n" +
            "          \"aggregate_rating\": \"4.9\",\n" +
            "          \"rating_text\": \"Excellent\",\n" +
            "          \"rating_color\": \"3F7E00\",\n" +
            "          \"votes\": \"1546\"\n" +
            "        },\n" +
            "        \"photos_url\": \"https://www.zomato.com/new-york-city/shake-shack-upper-west-side/photos?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1#tabtop\",\n" +
            "        \"menu_url\": \"https://www.zomato.com/new-york-city/shake-shack-upper-west-side/menu?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1&openSwipeBox=menu&showMinimal=1#tabtop\",\n" +
            "        \"featured_image\": \"https://b.zmtcdn.com/data/res_imagery/16777384_CHAIN_10d8440cfa6b530875c2cc6067e31349_c.jpg\",\n" +
            "        \"has_online_delivery\": 0,\n" +
            "        \"is_delivering_now\": 0,\n" +
            "        \"deeplink\": \"zomato://restaurant/16783153\",\n" +
            "        \"has_table_booking\": 0,\n" +
            "        \"events_url\": \"https://www.zomato.com/new-york-city/shake-shack-upper-west-side/events#tabtop?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"establishment_types\": []\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"restaurant\": {\n" +
            "        \"R\": {\n" +
            "          \"res_id\": 16771079\n" +
            "        },\n" +
            "        \"apikey\": \"ff9233307cb49a3287dcdc2c8daed54c\",\n" +
            "        \"id\": \"16771079\",\n" +
            "        \"name\": \"Lombardi's Pizza\",\n" +
            "        \"url\": \"https://www.zomato.com/new-york-city/lombardis-pizza-lower-east-side?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"location\": {\n" +
            "          \"address\": \"32 Spring Street, New York 10012\",\n" +
            "          \"locality\": \"Spring Street, Lower East Side\",\n" +
            "          \"city\": \"New York City\",\n" +
            "          \"city_id\": 280,\n" +
            "          \"latitude\": \"40.7216750000\",\n" +
            "          \"longitude\": \"-73.9955888889\",\n" +
            "          \"zipcode\": \"10012\",\n" +
            "          \"country_id\": 216,\n" +
            "          \"locality_verbose\": \"Spring Street, Lower East Side, New York City\"\n" +
            "        },\n" +
            "        \"switch_to_order_menu\": 0,\n" +
            "        \"cuisines\": \"Pizza, Italian\",\n" +
            "        \"average_cost_for_two\": 50,\n" +
            "        \"price_range\": 3,\n" +
            "        \"currency\": \"$\",\n" +
            "        \"offers\": [],\n" +
            "        \"thumb\": \"https://b.zmtcdn.com/data/res_imagery/16771079_RESTAURANT_da60c9abb32fa64cddc148a2795ae43c_c.jpg?fit=around%7C200%3A200&crop=200%3A200%3B%2A%2C%2A\",\n" +
            "        \"user_rating\": {\n" +
            "          \"aggregate_rating\": \"4.9\",\n" +
            "          \"rating_text\": \"Excellent\",\n" +
            "          \"rating_color\": \"3F7E00\",\n" +
            "          \"votes\": \"2143\"\n" +
            "        },\n" +
            "        \"photos_url\": \"https://www.zomato.com/new-york-city/lombardis-pizza-lower-east-side/photos?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1#tabtop\",\n" +
            "        \"menu_url\": \"https://www.zomato.com/new-york-city/lombardis-pizza-lower-east-side/menu?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1&openSwipeBox=menu&showMinimal=1#tabtop\",\n" +
            "        \"featured_image\": \"https://b.zmtcdn.com/data/res_imagery/16771079_RESTAURANT_da60c9abb32fa64cddc148a2795ae43c_c.jpg\",\n" +
            "        \"has_online_delivery\": 0,\n" +
            "        \"is_delivering_now\": 0,\n" +
            "        \"deeplink\": \"zomato://restaurant/16771079\",\n" +
            "        \"has_table_booking\": 0,\n" +
            "        \"events_url\": \"https://www.zomato.com/new-york-city/lombardis-pizza-lower-east-side/events#tabtop?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"establishment_types\": []\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"restaurant\": {\n" +
            "        \"R\": {\n" +
            "          \"res_id\": 16781875\n" +
            "        },\n" +
            "        \"apikey\": \"ff9233307cb49a3287dcdc2c8daed54c\",\n" +
            "        \"id\": \"16781875\",\n" +
            "        \"name\": \"Ippudo\",\n" +
            "        \"url\": \"https://www.zomato.com/new-york-city/ippudo-east-village?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"location\": {\n" +
            "          \"address\": \"65 4th Avenue, New York 10003\",\n" +
            "          \"locality\": \"East Village\",\n" +
            "          \"city\": \"New York City\",\n" +
            "          \"city_id\": 280,\n" +
            "          \"latitude\": \"40.7307361111\",\n" +
            "          \"longitude\": \"-73.9906833333\",\n" +
            "          \"zipcode\": \"10003\",\n" +
            "          \"country_id\": 216,\n" +
            "          \"locality_verbose\": \"East Village\"\n" +
            "        },\n" +
            "        \"switch_to_order_menu\": 0,\n" +
            "        \"cuisines\": \"Ramen\",\n" +
            "        \"average_cost_for_two\": 40,\n" +
            "        \"price_range\": 3,\n" +
            "        \"currency\": \"$\",\n" +
            "        \"offers\": [],\n" +
            "        \"thumb\": \"https://b.zmtcdn.com/data/res_imagery/16781875_RESTAURANT_d0f7a0565b23d07be15d3e8326d41066.jpg?fit=around%7C200%3A200&crop=200%3A200%3B%2A%2C%2A\",\n" +
            "        \"user_rating\": {\n" +
            "          \"aggregate_rating\": \"4.8\",\n" +
            "          \"rating_text\": \"Excellent\",\n" +
            "          \"rating_color\": \"3F7E00\",\n" +
            "          \"votes\": \"1375\"\n" +
            "        },\n" +
            "        \"photos_url\": \"https://www.zomato.com/new-york-city/ippudo-east-village/photos?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1#tabtop\",\n" +
            "        \"menu_url\": \"https://www.zomato.com/new-york-city/ippudo-east-village/menu?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1&openSwipeBox=menu&showMinimal=1#tabtop\",\n" +
            "        \"featured_image\": \"https://b.zmtcdn.com/data/res_imagery/16781875_RESTAURANT_d0f7a0565b23d07be15d3e8326d41066.jpg\",\n" +
            "        \"has_online_delivery\": 0,\n" +
            "        \"is_delivering_now\": 0,\n" +
            "        \"deeplink\": \"zomato://restaurant/16781875\",\n" +
            "        \"has_table_booking\": 0,\n" +
            "        \"events_url\": \"https://www.zomato.com/new-york-city/ippudo-east-village/events#tabtop?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"establishment_types\": []\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"restaurant\": {\n" +
            "        \"R\": {\n" +
            "          \"res_id\": 16762160\n" +
            "        },\n" +
            "        \"apikey\": \"ff9233307cb49a3287dcdc2c8daed54c\",\n" +
            "        \"id\": \"16762160\",\n" +
            "        \"name\": \"Carmine's Italian Restaurant - Times Square\",\n" +
            "        \"url\": \"https://www.zomato.com/new-york-city/carmines-italian-restaurant-times-square-gramercy?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"location\": {\n" +
            "          \"address\": \"200 W 44th Street, New York 10036\",\n" +
            "          \"locality\": \"West 44th Street, Gramercy\",\n" +
            "          \"city\": \"New York City\",\n" +
            "          \"city_id\": 280,\n" +
            "          \"latitude\": \"40.7574980000\",\n" +
            "          \"longitude\": \"-73.9866540000\",\n" +
            "          \"zipcode\": \"10036\",\n" +
            "          \"country_id\": 216,\n" +
            "          \"locality_verbose\": \"West 44th Street, Gramercy, New York City\"\n" +
            "        },\n" +
            "        \"switch_to_order_menu\": 0,\n" +
            "        \"cuisines\": \"Italian, Southern\",\n" +
            "        \"average_cost_for_two\": 100,\n" +
            "        \"price_range\": 4,\n" +
            "        \"currency\": \"$\",\n" +
            "        \"offers\": [],\n" +
            "        \"thumb\": \"\",\n" +
            "        \"user_rating\": {\n" +
            "          \"aggregate_rating\": \"4.7\",\n" +
            "          \"rating_text\": \"Excellent\",\n" +
            "          \"rating_color\": \"3F7E00\",\n" +
            "          \"votes\": \"2009\"\n" +
            "        },\n" +
            "        \"photos_url\": \"https://www.zomato.com/new-york-city/carmines-italian-restaurant-times-square-gramercy/photos?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1#tabtop\",\n" +
            "        \"menu_url\": \"https://www.zomato.com/new-york-city/carmines-italian-restaurant-times-square-gramercy/menu?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1&openSwipeBox=menu&showMinimal=1#tabtop\",\n" +
            "        \"featured_image\": \"\",\n" +
            "        \"has_online_delivery\": 0,\n" +
            "        \"is_delivering_now\": 0,\n" +
            "        \"deeplink\": \"zomato://restaurant/16762160\",\n" +
            "        \"has_table_booking\": 0,\n" +
            "        \"events_url\": \"https://www.zomato.com/new-york-city/carmines-italian-restaurant-times-square-gramercy/events#tabtop?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"establishment_types\": []\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"restaurant\": {\n" +
            "        \"R\": {\n" +
            "          \"res_id\": 16760367\n" +
            "        },\n" +
            "        \"apikey\": \"ff9233307cb49a3287dcdc2c8daed54c\",\n" +
            "        \"id\": \"16760367\",\n" +
            "        \"name\": \"Becco\",\n" +
            "        \"url\": \"https://www.zomato.com/new-york-city/becco-chelsea-manhattan?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"location\": {\n" +
            "          \"address\": \"355 W 46th Street, Theater District 10036\",\n" +
            "          \"locality\": \"Chelsea, Manhattan\",\n" +
            "          \"city\": \"New York City\",\n" +
            "          \"city_id\": 280,\n" +
            "          \"latitude\": \"40.7606600000\",\n" +
            "          \"longitude\": \"-73.9894300000\",\n" +
            "          \"zipcode\": \"10036\",\n" +
            "          \"country_id\": 216,\n" +
            "          \"locality_verbose\": \"Chelsea, Manhattan\"\n" +
            "        },\n" +
            "        \"switch_to_order_menu\": 0,\n" +
            "        \"cuisines\": \"Italian\",\n" +
            "        \"average_cost_for_two\": 110,\n" +
            "        \"price_range\": 4,\n" +
            "        \"currency\": \"$\",\n" +
            "        \"offers\": [],\n" +
            "        \"thumb\": \"https://b.zmtcdn.com/data/res_imagery/16760367_RESTAURANT_8d0ff25f51f5f31bd4e64085821420a6.jpg?fit=around%7C200%3A200&crop=200%3A200%3B%2A%2C%2A\",\n" +
            "        \"user_rating\": {\n" +
            "          \"aggregate_rating\": \"4.5\",\n" +
            "          \"rating_text\": \"Excellent\",\n" +
            "          \"rating_color\": \"3F7E00\",\n" +
            "          \"votes\": \"1251\"\n" +
            "        },\n" +
            "        \"photos_url\": \"https://www.zomato.com/new-york-city/becco-chelsea-manhattan/photos?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1#tabtop\",\n" +
            "        \"menu_url\": \"https://www.zomato.com/new-york-city/becco-chelsea-manhattan/menu?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1&openSwipeBox=menu&showMinimal=1#tabtop\",\n" +
            "        \"featured_image\": \"https://b.zmtcdn.com/data/res_imagery/16760367_RESTAURANT_8d0ff25f51f5f31bd4e64085821420a6.jpg\",\n" +
            "        \"has_online_delivery\": 0,\n" +
            "        \"is_delivering_now\": 0,\n" +
            "        \"deeplink\": \"zomato://restaurant/16760367\",\n" +
            "        \"has_table_booking\": 0,\n" +
            "        \"events_url\": \"https://www.zomato.com/new-york-city/becco-chelsea-manhattan/events#tabtop?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"establishment_types\": []\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"restaurant\": {\n" +
            "        \"R\": {\n" +
            "          \"res_id\": 16765367\n" +
            "        },\n" +
            "        \"apikey\": \"ff9233307cb49a3287dcdc2c8daed54c\",\n" +
            "        \"id\": \"16765367\",\n" +
            "        \"name\": \"Eleven Madison Park\",\n" +
            "        \"url\": \"https://www.zomato.com/new-york-city/eleven-madison-park-gramercy-flatiron?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"location\": {\n" +
            "          \"address\": \"11 Madison Avenue, New York 10010\",\n" +
            "          \"locality\": \"Gramercy-Flatiron\",\n" +
            "          \"city\": \"New York City\",\n" +
            "          \"city_id\": 280,\n" +
            "          \"latitude\": \"40.7415086000\",\n" +
            "          \"longitude\": \"-73.9866285000\",\n" +
            "          \"zipcode\": \"10010\",\n" +
            "          \"country_id\": 216,\n" +
            "          \"locality_verbose\": \"Gramercy-Flatiron\"\n" +
            "        },\n" +
            "        \"switch_to_order_menu\": 0,\n" +
            "        \"cuisines\": \"New American\",\n" +
            "        \"average_cost_for_two\": 600,\n" +
            "        \"price_range\": 4,\n" +
            "        \"currency\": \"$\",\n" +
            "        \"offers\": [],\n" +
            "        \"thumb\": \"https://b.zmtcdn.com/data/res_imagery/16765367_RESTAURANT_bd2d075a982733fa099b3f00c43e30e1_c.jpg?fit=around%7C200%3A200&crop=200%3A200%3B%2A%2C%2A\",\n" +
            "        \"user_rating\": {\n" +
            "          \"aggregate_rating\": \"4.7\",\n" +
            "          \"rating_text\": \"Excellent\",\n" +
            "          \"rating_color\": \"3F7E00\",\n" +
            "          \"votes\": \"731\"\n" +
            "        },\n" +
            "        \"photos_url\": \"https://www.zomato.com/new-york-city/eleven-madison-park-gramercy-flatiron/photos?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1#tabtop\",\n" +
            "        \"menu_url\": \"https://www.zomato.com/new-york-city/eleven-madison-park-gramercy-flatiron/menu?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1&openSwipeBox=menu&showMinimal=1#tabtop\",\n" +
            "        \"featured_image\": \"https://b.zmtcdn.com/data/res_imagery/16765367_RESTAURANT_bd2d075a982733fa099b3f00c43e30e1_c.jpg\",\n" +
            "        \"has_online_delivery\": 0,\n" +
            "        \"is_delivering_now\": 0,\n" +
            "        \"deeplink\": \"zomato://restaurant/16765367\",\n" +
            "        \"has_table_booking\": 0,\n" +
            "        \"events_url\": \"https://www.zomato.com/new-york-city/eleven-madison-park-gramercy-flatiron/events#tabtop?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"establishment_types\": []\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"restaurant\": {\n" +
            "        \"R\": {\n" +
            "          \"res_id\": 16785398\n" +
            "        },\n" +
            "        \"apikey\": \"ff9233307cb49a3287dcdc2c8daed54c\",\n" +
            "        \"id\": \"16785398\",\n" +
            "        \"name\": \"Shake Shack\",\n" +
            "        \"url\": \"https://www.zomato.com/new-york-city/shake-shack-hells-kitchen?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"location\": {\n" +
            "          \"address\": \"691 8th Avenue, New York 10036\",\n" +
            "          \"locality\": \"Hell's Kitchen\",\n" +
            "          \"city\": \"New York City\",\n" +
            "          \"city_id\": 280,\n" +
            "          \"latitude\": \"40.7587361111\",\n" +
            "          \"longitude\": \"-73.9890138889\",\n" +
            "          \"zipcode\": \"10036\",\n" +
            "          \"country_id\": 216,\n" +
            "          \"locality_verbose\": \"Hell's Kitchen\"\n" +
            "        },\n" +
            "        \"switch_to_order_menu\": 0,\n" +
            "        \"cuisines\": \"American, Burger\",\n" +
            "        \"average_cost_for_two\": 30,\n" +
            "        \"price_range\": 2,\n" +
            "        \"currency\": \"$\",\n" +
            "        \"offers\": [],\n" +
            "        \"thumb\": \"https://b.zmtcdn.com/data/res_imagery/16785398_RESTAURANT_9808b948d2739435ea95bd3002d95cda.jpg?fit=around%7C200%3A200&crop=200%3A200%3B%2A%2C%2A\",\n" +
            "        \"user_rating\": {\n" +
            "          \"aggregate_rating\": \"4.9\",\n" +
            "          \"rating_text\": \"Excellent\",\n" +
            "          \"rating_color\": \"3F7E00\",\n" +
            "          \"votes\": \"948\"\n" +
            "        },\n" +
            "        \"photos_url\": \"https://www.zomato.com/new-york-city/shake-shack-hells-kitchen/photos?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1#tabtop\",\n" +
            "        \"menu_url\": \"https://www.zomato.com/new-york-city/shake-shack-hells-kitchen/menu?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1&openSwipeBox=menu&showMinimal=1#tabtop\",\n" +
            "        \"featured_image\": \"https://b.zmtcdn.com/data/res_imagery/16785398_RESTAURANT_9808b948d2739435ea95bd3002d95cda.jpg\",\n" +
            "        \"has_online_delivery\": 0,\n" +
            "        \"is_delivering_now\": 0,\n" +
            "        \"deeplink\": \"zomato://restaurant/16785398\",\n" +
            "        \"has_table_booking\": 0,\n" +
            "        \"events_url\": \"https://www.zomato.com/new-york-city/shake-shack-hells-kitchen/events#tabtop?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"establishment_types\": []\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"restaurant\": {\n" +
            "        \"R\": {\n" +
            "          \"res_id\": 16775039\n" +
            "        },\n" +
            "        \"apikey\": \"ff9233307cb49a3287dcdc2c8daed54c\",\n" +
            "        \"id\": \"16775039\",\n" +
            "        \"name\": \"Peter Luger Steak House\",\n" +
            "        \"url\": \"https://www.zomato.com/new-york-city/peter-luger-steak-house-south-side?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"location\": {\n" +
            "          \"address\": \"178 Broadway, Brooklyn 11211\",\n" +
            "          \"locality\": \"South Side\",\n" +
            "          \"city\": \"New York City\",\n" +
            "          \"city_id\": 280,\n" +
            "          \"latitude\": \"40.7098777778\",\n" +
            "          \"longitude\": \"-73.9623416667\",\n" +
            "          \"zipcode\": \"11211\",\n" +
            "          \"country_id\": 216,\n" +
            "          \"locality_verbose\": \"South Side\"\n" +
            "        },\n" +
            "        \"switch_to_order_menu\": 0,\n" +
            "        \"cuisines\": \"Steak, American, German\",\n" +
            "        \"average_cost_for_two\": 150,\n" +
            "        \"price_range\": 4,\n" +
            "        \"currency\": \"$\",\n" +
            "        \"offers\": [],\n" +
            "        \"thumb\": \"https://b.zmtcdn.com/data/res_imagery/16775039_RESTAURANT_18bb213a181da7fe5479eeefe2adfa66.jpg?fit=around%7C200%3A200&crop=200%3A200%3B%2A%2C%2A\",\n" +
            "        \"user_rating\": {\n" +
            "          \"aggregate_rating\": \"4.6\",\n" +
            "          \"rating_text\": \"Excellent\",\n" +
            "          \"rating_color\": \"3F7E00\",\n" +
            "          \"votes\": \"2124\"\n" +
            "        },\n" +
            "        \"photos_url\": \"https://www.zomato.com/new-york-city/peter-luger-steak-house-south-side/photos?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1#tabtop\",\n" +
            "        \"menu_url\": \"https://www.zomato.com/new-york-city/peter-luger-steak-house-south-side/menu?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1&openSwipeBox=menu&showMinimal=1#tabtop\",\n" +
            "        \"featured_image\": \"https://b.zmtcdn.com/data/res_imagery/16775039_RESTAURANT_18bb213a181da7fe5479eeefe2adfa66.jpg\",\n" +
            "        \"has_online_delivery\": 0,\n" +
            "        \"is_delivering_now\": 0,\n" +
            "        \"deeplink\": \"zomato://restaurant/16775039\",\n" +
            "        \"has_table_booking\": 0,\n" +
            "        \"events_url\": \"https://www.zomato.com/new-york-city/peter-luger-steak-house-south-side/events#tabtop?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"establishment_types\": []\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"restaurant\": {\n" +
            "        \"R\": {\n" +
            "          \"res_id\": 16774980\n" +
            "        },\n" +
            "        \"apikey\": \"ff9233307cb49a3287dcdc2c8daed54c\",\n" +
            "        \"id\": \"16774980\",\n" +
            "        \"name\": \"Per Se\",\n" +
            "        \"url\": \"https://www.zomato.com/new-york-city/per-se-hells-kitchen?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"location\": {\n" +
            "          \"address\": \"Time Warner Center, 10 Columbus Circle, 4th Floor, New York 10019\",\n" +
            "          \"locality\": \"Columbus Circle, Hell's Kitchen\",\n" +
            "          \"city\": \"New York City\",\n" +
            "          \"city_id\": 280,\n" +
            "          \"latitude\": \"40.7684141000\",\n" +
            "          \"longitude\": \"-73.9827037000\",\n" +
            "          \"zipcode\": \"10019\",\n" +
            "          \"country_id\": 216,\n" +
            "          \"locality_verbose\": \"Columbus Circle, Hell's Kitchen, New York City\"\n" +
            "        },\n" +
            "        \"switch_to_order_menu\": 0,\n" +
            "        \"cuisines\": \"French\",\n" +
            "        \"average_cost_for_two\": 500,\n" +
            "        \"price_range\": 4,\n" +
            "        \"currency\": \"$\",\n" +
            "        \"offers\": [],\n" +
            "        \"thumb\": \"https://b.zmtcdn.com/data/res_imagery/16774980_RESTAURANT_b010c67d47227f27aa142dfd13adaa3e_c.png?fit=around%7C200%3A200&crop=200%3A200%3B%2A%2C%2A\",\n" +
            "        \"user_rating\": {\n" +
            "          \"aggregate_rating\": \"4.4\",\n" +
            "          \"rating_text\": \"Very Good\",\n" +
            "          \"rating_color\": \"5BA829\",\n" +
            "          \"votes\": \"720\"\n" +
            "        },\n" +
            "        \"photos_url\": \"https://www.zomato.com/new-york-city/per-se-hells-kitchen/photos?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1#tabtop\",\n" +
            "        \"menu_url\": \"https://www.zomato.com/new-york-city/per-se-hells-kitchen/menu?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1&openSwipeBox=menu&showMinimal=1#tabtop\",\n" +
            "        \"featured_image\": \"https://b.zmtcdn.com/data/res_imagery/16774980_RESTAURANT_b010c67d47227f27aa142dfd13adaa3e_c.png\",\n" +
            "        \"has_online_delivery\": 0,\n" +
            "        \"is_delivering_now\": 0,\n" +
            "        \"deeplink\": \"zomato://restaurant/16774980\",\n" +
            "        \"has_table_booking\": 0,\n" +
            "        \"events_url\": \"https://www.zomato.com/new-york-city/per-se-hells-kitchen/events#tabtop?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"establishment_types\": []\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"restaurant\": {\n" +
            "        \"R\": {\n" +
            "          \"res_id\": 16767332\n" +
            "        },\n" +
            "        \"apikey\": \"ff9233307cb49a3287dcdc2c8daed54c\",\n" +
            "        \"id\": \"16767332\",\n" +
            "        \"name\": \"Grimaldi's Pizzeria\",\n" +
            "        \"url\": \"https://www.zomato.com/new-york-city/grimaldis-pizzeria-dumbo?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"location\": {\n" +
            "          \"address\": \"1 Front Street, Brooklyn 11201\",\n" +
            "          \"locality\": \"Front Street, DUMBO\",\n" +
            "          \"city\": \"New York City\",\n" +
            "          \"city_id\": 280,\n" +
            "          \"latitude\": \"40.7025138889\",\n" +
            "          \"longitude\": \"-73.9932722222\",\n" +
            "          \"zipcode\": \"11201\",\n" +
            "          \"country_id\": 216,\n" +
            "          \"locality_verbose\": \"Front Street, DUMBO, New York City\"\n" +
            "        },\n" +
            "        \"switch_to_order_menu\": 0,\n" +
            "        \"cuisines\": \"Pizza, Italian\",\n" +
            "        \"average_cost_for_two\": 50,\n" +
            "        \"price_range\": 3,\n" +
            "        \"currency\": \"$\",\n" +
            "        \"offers\": [],\n" +
            "        \"thumb\": \"https://b.zmtcdn.com/data/res_imagery/16767332_RESTAURANT_3e45ed0b49952f5dd0f7ba4c98b9ab2c_c.jpg?fit=around%7C200%3A200&crop=200%3A200%3B%2A%2C%2A\",\n" +
            "        \"user_rating\": {\n" +
            "          \"aggregate_rating\": \"4.5\",\n" +
            "          \"rating_text\": \"Excellent\",\n" +
            "          \"rating_color\": \"3F7E00\",\n" +
            "          \"votes\": \"1630\"\n" +
            "        },\n" +
            "        \"photos_url\": \"https://www.zomato.com/new-york-city/grimaldis-pizzeria-dumbo/photos?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1#tabtop\",\n" +
            "        \"menu_url\": \"https://www.zomato.com/new-york-city/grimaldis-pizzeria-dumbo/menu?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1&openSwipeBox=menu&showMinimal=1#tabtop\",\n" +
            "        \"featured_image\": \"https://b.zmtcdn.com/data/res_imagery/16767332_RESTAURANT_3e45ed0b49952f5dd0f7ba4c98b9ab2c_c.jpg?output-format=webp\",\n" +
            "        \"has_online_delivery\": 0,\n" +
            "        \"is_delivering_now\": 0,\n" +
            "        \"deeplink\": \"zomato://restaurant/16767332\",\n" +
            "        \"has_table_booking\": 0,\n" +
            "        \"events_url\": \"https://www.zomato.com/new-york-city/grimaldis-pizzeria-dumbo/events#tabtop?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"establishment_types\": []\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"restaurant\": {\n" +
            "        \"R\": {\n" +
            "          \"res_id\": 16777961\n" +
            "        },\n" +
            "        \"apikey\": \"ff9233307cb49a3287dcdc2c8daed54c\",\n" +
            "        \"id\": \"16777961\",\n" +
            "        \"name\": \"The Spotted Pig\",\n" +
            "        \"url\": \"https://www.zomato.com/new-york-city/the-spotted-pig-greenwich-village?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"location\": {\n" +
            "          \"address\": \"314 W 11th Street, New York 10014\",\n" +
            "          \"locality\": \"Greenwich Village\",\n" +
            "          \"city\": \"New York City\",\n" +
            "          \"city_id\": 280,\n" +
            "          \"latitude\": \"40.7356916667\",\n" +
            "          \"longitude\": \"-74.0067222222\",\n" +
            "          \"zipcode\": \"10014\",\n" +
            "          \"country_id\": 216,\n" +
            "          \"locality_verbose\": \"Greenwich Village\"\n" +
            "        },\n" +
            "        \"switch_to_order_menu\": 0,\n" +
            "        \"cuisines\": \"Burger\",\n" +
            "        \"average_cost_for_two\": 70,\n" +
            "        \"price_range\": 4,\n" +
            "        \"currency\": \"$\",\n" +
            "        \"offers\": [],\n" +
            "        \"thumb\": \"https://b.zmtcdn.com/data/res_imagery/16777961_RESTAURANT_78c8dd0aaad6372b95d13f02291141d8_c.jpg?fit=around%7C200%3A200&crop=200%3A200%3B%2A%2C%2A\",\n" +
            "        \"user_rating\": {\n" +
            "          \"aggregate_rating\": \"4.6\",\n" +
            "          \"rating_text\": \"Excellent\",\n" +
            "          \"rating_color\": \"3F7E00\",\n" +
            "          \"votes\": \"2162\"\n" +
            "        },\n" +
            "        \"photos_url\": \"https://www.zomato.com/new-york-city/the-spotted-pig-greenwich-village/photos?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1#tabtop\",\n" +
            "        \"menu_url\": \"https://www.zomato.com/new-york-city/the-spotted-pig-greenwich-village/menu?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1&openSwipeBox=menu&showMinimal=1#tabtop\",\n" +
            "        \"featured_image\": \"https://b.zmtcdn.com/data/res_imagery/16777961_RESTAURANT_78c8dd0aaad6372b95d13f02291141d8_c.jpg?output-format=webp\",\n" +
            "        \"has_online_delivery\": 0,\n" +
            "        \"is_delivering_now\": 0,\n" +
            "        \"deeplink\": \"zomato://restaurant/16777961\",\n" +
            "        \"has_table_booking\": 0,\n" +
            "        \"events_url\": \"https://www.zomato.com/new-york-city/the-spotted-pig-greenwich-village/events#tabtop?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"establishment_types\": []\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"restaurant\": {\n" +
            "        \"R\": {\n" +
            "          \"res_id\": 16781904\n" +
            "        },\n" +
            "        \"apikey\": \"ff9233307cb49a3287dcdc2c8daed54c\",\n" +
            "        \"id\": \"16781904\",\n" +
            "        \"name\": \"Momofuku Noodle Bar\",\n" +
            "        \"url\": \"https://www.zomato.com/new-york-city/momofuku-noodle-bar-east-village?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"location\": {\n" +
            "          \"address\": \"171 1st Avenue, New York 10003\",\n" +
            "          \"locality\": \"East Village\",\n" +
            "          \"city\": \"New York City\",\n" +
            "          \"city_id\": 280,\n" +
            "          \"latitude\": \"40.7291416667\",\n" +
            "          \"longitude\": \"-73.9842388889\",\n" +
            "          \"zipcode\": \"10003\",\n" +
            "          \"country_id\": 216,\n" +
            "          \"locality_verbose\": \"East Village\"\n" +
            "        },\n" +
            "        \"switch_to_order_menu\": 0,\n" +
            "        \"cuisines\": \"Asian, Ramen\",\n" +
            "        \"average_cost_for_two\": 70,\n" +
            "        \"price_range\": 4,\n" +
            "        \"currency\": \"$\",\n" +
            "        \"offers\": [],\n" +
            "        \"thumb\": \"https://b.zmtcdn.com/data/res_imagery/16781904_RESTAURANT_fe3097ac80f3396f383f8ba588bcf832_c.jpg?fit=around%7C200%3A200&crop=200%3A200%3B%2A%2C%2A\",\n" +
            "        \"user_rating\": {\n" +
            "          \"aggregate_rating\": \"4.7\",\n" +
            "          \"rating_text\": \"Excellent\",\n" +
            "          \"rating_color\": \"3F7E00\",\n" +
            "          \"votes\": \"1620\"\n" +
            "        },\n" +
            "        \"photos_url\": \"https://www.zomato.com/new-york-city/momofuku-noodle-bar-east-village/photos?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1#tabtop\",\n" +
            "        \"menu_url\": \"https://www.zomato.com/new-york-city/momofuku-noodle-bar-east-village/menu?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1&openSwipeBox=menu&showMinimal=1#tabtop\",\n" +
            "        \"featured_image\": \"https://b.zmtcdn.com/data/res_imagery/16781904_RESTAURANT_fe3097ac80f3396f383f8ba588bcf832_c.jpg\",\n" +
            "        \"has_online_delivery\": 0,\n" +
            "        \"is_delivering_now\": 0,\n" +
            "        \"deeplink\": \"zomato://restaurant/16781904\",\n" +
            "        \"has_table_booking\": 0,\n" +
            "        \"events_url\": \"https://www.zomato.com/new-york-city/momofuku-noodle-bar-east-village/events#tabtop?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"establishment_types\": []\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"restaurant\": {\n" +
            "        \"R\": {\n" +
            "          \"res_id\": 16761402\n" +
            "        },\n" +
            "        \"apikey\": \"ff9233307cb49a3287dcdc2c8daed54c\",\n" +
            "        \"id\": \"16761402\",\n" +
            "        \"name\": \"Burger Joint\",\n" +
            "        \"url\": \"https://www.zomato.com/new-york-city/burger-joint-midtown?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"location\": {\n" +
            "          \"address\": \"Le Parker Meridien Hotel, 119 W 56th Street, New York 10019\",\n" +
            "          \"locality\": \"Midtown\",\n" +
            "          \"city\": \"New York City\",\n" +
            "          \"city_id\": 280,\n" +
            "          \"latitude\": \"40.7641849000\",\n" +
            "          \"longitude\": \"-73.9784051000\",\n" +
            "          \"zipcode\": \"10019\",\n" +
            "          \"country_id\": 216,\n" +
            "          \"locality_verbose\": \"Midtown\"\n" +
            "        },\n" +
            "        \"switch_to_order_menu\": 0,\n" +
            "        \"cuisines\": \"Burger\",\n" +
            "        \"average_cost_for_two\": 25,\n" +
            "        \"price_range\": 2,\n" +
            "        \"currency\": \"$\",\n" +
            "        \"offers\": [],\n" +
            "        \"thumb\": \"\",\n" +
            "        \"user_rating\": {\n" +
            "          \"aggregate_rating\": \"4.7\",\n" +
            "          \"rating_text\": \"Excellent\",\n" +
            "          \"rating_color\": \"3F7E00\",\n" +
            "          \"votes\": \"1456\"\n" +
            "        },\n" +
            "        \"photos_url\": \"https://www.zomato.com/new-york-city/burger-joint-midtown/photos?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1#tabtop\",\n" +
            "        \"menu_url\": \"https://www.zomato.com/new-york-city/burger-joint-midtown/menu?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1&openSwipeBox=menu&showMinimal=1#tabtop\",\n" +
            "        \"featured_image\": \"\",\n" +
            "        \"has_online_delivery\": 0,\n" +
            "        \"is_delivering_now\": 0,\n" +
            "        \"deeplink\": \"zomato://restaurant/16761402\",\n" +
            "        \"has_table_booking\": 0,\n" +
            "        \"events_url\": \"https://www.zomato.com/new-york-city/burger-joint-midtown/events#tabtop?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"establishment_types\": []\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"restaurant\": {\n" +
            "        \"R\": {\n" +
            "          \"res_id\": 16780467\n" +
            "        },\n" +
            "        \"apikey\": \"ff9233307cb49a3287dcdc2c8daed54c\",\n" +
            "        \"id\": \"16780467\",\n" +
            "        \"name\": \"Veselka\",\n" +
            "        \"url\": \"https://www.zomato.com/new-york-city/veselka-east-village?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"location\": {\n" +
            "          \"address\": \"144 2nd Avenue, New York 10003\",\n" +
            "          \"locality\": \"East Village\",\n" +
            "          \"city\": \"New York City\",\n" +
            "          \"city_id\": 280,\n" +
            "          \"latitude\": \"40.7292166667\",\n" +
            "          \"longitude\": \"-73.9872277778\",\n" +
            "          \"zipcode\": \"10003\",\n" +
            "          \"country_id\": 216,\n" +
            "          \"locality_verbose\": \"East Village\"\n" +
            "        },\n" +
            "        \"switch_to_order_menu\": 0,\n" +
            "        \"cuisines\": \"Ukrainian\",\n" +
            "        \"average_cost_for_two\": 40,\n" +
            "        \"price_range\": 3,\n" +
            "        \"currency\": \"$\",\n" +
            "        \"offers\": [],\n" +
            "        \"thumb\": \"https://b.zmtcdn.com/data/res_imagery/16780467_RESTAURANT_6358ddba766cd068451b21e01fa4a6f4_c.jpg?fit=around%7C200%3A200&crop=200%3A200%3B%2A%2C%2A\",\n" +
            "        \"user_rating\": {\n" +
            "          \"aggregate_rating\": \"4.4\",\n" +
            "          \"rating_text\": \"Very Good\",\n" +
            "          \"rating_color\": \"5BA829\",\n" +
            "          \"votes\": \"1276\"\n" +
            "        },\n" +
            "        \"photos_url\": \"https://www.zomato.com/new-york-city/veselka-east-village/photos?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1#tabtop\",\n" +
            "        \"menu_url\": \"https://www.zomato.com/new-york-city/veselka-east-village/menu?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1&openSwipeBox=menu&showMinimal=1#tabtop\",\n" +
            "        \"featured_image\": \"https://b.zmtcdn.com/data/res_imagery/16780467_RESTAURANT_6358ddba766cd068451b21e01fa4a6f4_c.jpg\",\n" +
            "        \"has_online_delivery\": 0,\n" +
            "        \"is_delivering_now\": 0,\n" +
            "        \"deeplink\": \"zomato://restaurant/16780467\",\n" +
            "        \"has_table_booking\": 0,\n" +
            "        \"events_url\": \"https://www.zomato.com/new-york-city/veselka-east-village/events#tabtop?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"establishment_types\": []\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";
}
