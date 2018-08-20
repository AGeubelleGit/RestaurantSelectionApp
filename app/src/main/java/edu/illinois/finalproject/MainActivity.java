package edu.illinois.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import edu.illinois.finalproject.ZomatoAPIFiles.RestaurantContainer;
import edu.illinois.finalproject.ZomatoAPIFiles.RestaurantFirebaseContainer;
import edu.illinois.finalproject.firebaseauthentication.LoginActivity;
import edu.illinois.finalproject.grouplist.GroupListActivity;

public class MainActivity extends AppCompatActivity {

    public static String currId = "";
    public static String groupName = "";

    private ZomatoAdapter mAdapter;
    private RecyclerView mRestaurantList;

    private MainActivity main;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if(extras != null) {
            groupName = extras.getString("GroupName");
            currId = extras.getString("GroupId");
        }

        //Set the group name.
        final TextView groupNameTextView = (TextView) findViewById(R.id.group_name_text_view);
        groupNameTextView.setText(groupName);

        main = this;
        database = FirebaseDatabase.getInstance();

        final RecyclerView restaurantsRecycler = (RecyclerView) findViewById(R.id.rv_restaurants);
        DatabaseReference restaurantsRef = database.getReference(currId);

        //Create a recycler view adapter to process the restaurants in the firebase database.
        FirebaseRecyclerAdapter<RestaurantFirebaseContainer, FirebaseRestaurantViewHolder> restaurantAdapter =
                new FirebaseRecyclerAdapter<RestaurantFirebaseContainer, FirebaseRestaurantViewHolder>
                        (RestaurantFirebaseContainer.class, R.layout.zomato_restaurant_firebase_cell,
                                FirebaseRestaurantViewHolder.class, restaurantsRef) {

                    //Pass the RestaurantFirebaseContainer to the view holder.
                    @Override
                    protected void populateViewHolder(FirebaseRestaurantViewHolder viewHolder,
                                                      RestaurantFirebaseContainer model, int position) {
                        viewHolder.bind(position, model);
                    }
                };

        //Set restaurant recycler view's layout manager and adapter.
        restaurantsRecycler.setHasFixedSize(true);
        restaurantsRecycler.setLayoutManager(new LinearLayoutManager(this));
        restaurantsRecycler.setAdapter(restaurantAdapter);

        //When the button is clicked open the search activity.
        final Button searchButton = (Button) findViewById(R.id.main_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context context = view.getContext();
                Intent searchIntent = new Intent(context, SearchActivity.class);
                context.startActivity(searchIntent);
            }
        });

        //Return to list of groups.
        final Button groupButton = (Button) findViewById(R.id.main_groups_button);
        groupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GroupListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Logout Button.
        final Button logoutButton = (Button) findViewById(R.id.main_logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log out of firebase authentication.
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}