package edu.illinois.finalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.net.URLEncoder;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import edu.illinois.finalproject.ZomatoAPIFiles.RestaurantContainer;
import edu.illinois.finalproject.ZomatoAPIFiles.RestaurantFirebaseContainer;
import edu.illinois.finalproject.ZomatoAPIFiles.RestaurantL3;
import edu.illinois.finalproject.ZomatoAPIFiles.ZomatoSearchResult;

/**
 * Created by alexandregeubelle on 11/6/17.
 */

public class DetailActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        //Initialize View variables.
        final TextView nameTextView = (TextView) findViewById(R.id.detail_name_text);
        final Button websiteButton = (Button) findViewById(R.id.detail_website_button);
        final Button addressButton = (Button) findViewById(R.id.detail_address_button);
        final TextView cuisineTypesTextView = (TextView) findViewById(R.id.detail_cuisine_types_text);
        final TextView priceTextView = (TextView) findViewById(R.id.detail_price_text);
        final TextView priceForTwoTextView = (TextView) findViewById(R.id.price_for_two_text);
        final Button addRemoveButton = (Button) findViewById(R.id.detail_activity_action_button);

        //Get the intent that started this activity and the restaurant that was passed in.
        final Intent intent = getIntent();
        RestaurantL3 restaurant = intent.getParcelableExtra("restaurant");
        RestaurantActionType actionType = (RestaurantActionType) intent.getSerializableExtra("ActionType");


        //Get variables from the restaurant.
        final String name = restaurant.getName();
        final String websiteURL = restaurant.getUrl();
        final String address = restaurant.getLocation().getAddress();
        final String cuisineTypes = restaurant.getCuisines();
        final int price = restaurant.getPrice_range();
        final String currencyString = getResources().getString(R.string.currency_symbol);
        final int priceForTwo = restaurant.getAverage_cost_for_two();

        //Set the text views' text.
        nameTextView.setText(name);
        addressButton.setText(address);
        cuisineTypesTextView.setText(cuisineTypes);

        //Create String of a certain number of the currency String ("$$$$")
        StringBuilder priceString = new StringBuilder();
        for(int iterator = 0; iterator < price; iterator++) {
            priceString.append(currencyString);
        }
        priceTextView.setText(priceString.toString());

        //Example of this string will be "Price for two: $40"
        String priceForTwoString = getResources().getString(R.string.restaurant_price_for_two_default_text) +
                " " + currencyString + priceForTwo;
        priceForTwoTextView.setText(priceForTwoString);

        //Add onClick listeners to the webpage button.
        try {
            final Uri webpage = Uri.parse(websiteURL);
            AddWebsiteButtonOnClickListener(websiteButton, webpage);
        } catch (Exception e) {
            websiteButton.setEnabled(false);
            websiteButton.setOnClickListener(null);
        }

        //Add onClick listener to the address button.
        try {
            final String encodedAddress = URLEncoder.encode(address, "UTF-8");
            addAddressButtonOnClickListener(addressButton, encodedAddress);
        } catch (Exception e) {
            addressButton.setEnabled(false);
            addressButton.setOnClickListener(null);
        }

        //Set up button to add/remove
        if(actionType != null && actionType == RestaurantActionType.ADD) {
            AddActionAddButtonOnClickListener(addRemoveButton, restaurant);
        }else if(actionType != null && actionType == RestaurantActionType.REMOVE) {
            RemoveActionAddButtonOnClickListener(addRemoveButton, restaurant);
        }

    }

    private void addAddressButtonOnClickListener(Button addressButton, final String encodedAddress) {
        addressButton.setEnabled(true);

        addressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri locationUri = Uri.parse("geo:0,0?q=" + encodedAddress);
                Intent intent = new Intent(Intent.ACTION_VIEW, locationUri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    private void AddWebsiteButtonOnClickListener(Button websiteButton, final Uri webpage) {
        websiteButton.setEnabled(true);

        websiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    //Set up button to add the restaurant to the group list.
    public void AddActionAddButtonOnClickListener(Button actionButton, final RestaurantL3 toAdd) {
        //Set the button text.
        String addText = getResources().getString(R.string.add_action_button_text);
        actionButton.setText(addText);

        //Set up action for when add is pressed.
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get reference to the current group.
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference rootReference = database.getReference(MainActivity.currId);

                //Create new firebase restaurant container to add to database.
                final RestaurantFirebaseContainer restaurant = new RestaurantFirebaseContainer(toAdd);
                final String restaurantId = Integer.toString(restaurant.getRestaurant().getId());

                //If this restaurant is already in the database do nothing otherwise add the
                //new container to the database.
                rootReference.child(restaurantId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()) {
                            rootReference.child(restaurantId).setValue(restaurant);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Do nothing.
                    }
                });

                //Return to main activity.
                Intent returnToMainIntent = new Intent(getApplicationContext(),
                        MainActivity.class);
                startActivity(returnToMainIntent);
            }
        });
    }

    public void RemoveActionAddButtonOnClickListener(Button actionButton, final RestaurantL3 toRemove) {
        //Set button text.
        String addText = getResources().getString(R.string.remove_action_button_text);
        actionButton.setText(addText);

        //Set onclick to remove the restaurant from the group in the database.
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference rootReference = database.getReference(MainActivity.currId);

                String restaurantId = Integer.toString(toRemove.getId());
                rootReference.child(restaurantId).setValue(null);

                //Return to main activity.
                Intent returnToMainIntent = new Intent(getApplicationContext(),
                        MainActivity.class);
                startActivity(returnToMainIntent);
            }
        });
    }

}
