package edu.illinois.finalproject.grouplist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.illinois.finalproject.MainActivity;
import edu.illinois.finalproject.R;
import edu.illinois.finalproject.firebaseauthentication.LoginActivity;

/**
 * Created by alexandregeubelle on 12/7/17.
 */

public class GroupListActivity extends AppCompatActivity {

    private RecyclerView mGroupsList;

    private GroupListActivity main;
    private FirebaseDatabase database;

    // Based on:
    // https://www.androidhive.info/2016/06/android-getting-started-firebase-simple-login-registration-auth/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        main = this;
        database = FirebaseDatabase.getInstance();

        //Get reference the the recycler view and the database groups reference.
        final RecyclerView groupRecycler = (RecyclerView) findViewById(R.id.group_list_rv);
        DatabaseReference restaurantsRef = database.getReference("Groups");

        //Create a recycler adapter to display a recycler view of all the different groups.
        //Uses FirebaseGroupViewHolder.
        final FirebaseRecyclerAdapter<GroupInfo, FirebaseGroupViewHolder> restaurantAdapter =
                new FirebaseRecyclerAdapter<GroupInfo, FirebaseGroupViewHolder>
                        (GroupInfo.class, R.layout.group_cell,
                                FirebaseGroupViewHolder.class, restaurantsRef) {

                    //Passes the group info to the firebaseviewholder.
                    @Override
                    protected void populateViewHolder(FirebaseGroupViewHolder viewHolder,
                                                      GroupInfo model, int position) {
                        viewHolder.bind(model);
                    }
                };

        //Set the recycler views layout manager and adapter.
        groupRecycler.setHasFixedSize(true);
        groupRecycler.setLayoutManager(new LinearLayoutManager(this));
        groupRecycler.setAdapter(restaurantAdapter);

        //Set up logout button.
        Button logoutButton = (Button) findViewById(R.id.group_list_logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sign out the current user.
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                //Return to login activity.
                Intent intent = new Intent(GroupListActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Set up new group button.
        final Button newGroupButton = (Button) findViewById(R.id.group_list_new_group_button);
        newGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the new group activity.
                Intent intent = new Intent(GroupListActivity.this, NewGroupActivity.class);
                startActivity(intent);
            }
        });

    }
}