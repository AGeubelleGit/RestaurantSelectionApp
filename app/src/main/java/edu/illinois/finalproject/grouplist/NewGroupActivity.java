package edu.illinois.finalproject.grouplist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.illinois.finalproject.MainActivity;
import edu.illinois.finalproject.R;
import edu.illinois.finalproject.firebaseauthentication.LoginActivity;

/**
 * Created by alexandregeubelle on 12/8/17.
 */

public class NewGroupActivity extends AppCompatActivity {

    private final int nameLengthThreshold = 6;
    private final int passwordLengthThreshold = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        //Instantiate views.
        final EditText nameEditText = (EditText) findViewById(R.id.new_group_name_edit_text);
        final EditText passwordEditText = (EditText) findViewById(R.id.new_group_password_edit_text);
        final Button createButton = (Button) findViewById(R.id.new_group_create_button);
        final Button cancelButton = (Button) findViewById(R.id.new_group_cancel_button);

        //Return to group list activity.
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewGroupActivity.this, GroupListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get the current user's unique id and other user input information.
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if (auth.getCurrentUser() == null) {
                    return;
                }
                final String creatorUid = auth.getCurrentUser().getUid();
                final String name = nameEditText.getText().toString().trim();
                final String password = passwordEditText.getText().toString().trim();

                //If the password and group name meets minimum length.
                if(name.length() >= nameLengthThreshold && password.length() >= passwordLengthThreshold) {
                    //Get the two database references
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference rootReference = database.getReference("");
                    final DatabaseReference groupsReference = database.getReference("Groups");

                    //Create a new root child and save the key.
                    String newRootKey = rootReference.push().getKey();

                    //Add a new group to "Groups" with values.
                    DatabaseReference newGroupRef = groupsReference.push().getRef();
                    newGroupRef.child("Name").setValue(name);
                    newGroupRef.child("Password").setValue(password);
                    newGroupRef.child("Creator").setValue(creatorUid);
                    newGroupRef.child("Id").setValue(newRootKey);
                    newGroupRef.child("Key").setValue(newGroupRef.getKey());

                    //Set a base reference for the group with no restaurants in it
                    rootReference.child(newRootKey).setValue("No Restaurants");

                    //Go to the main activity for displaying the newly created group.
                    Intent intent = new Intent(NewGroupActivity.this, MainActivity.class);
                    intent.putExtra("GroupName", name);
                    intent.putExtra("GroupId", newRootKey);

                    startActivity(intent);
                    finish();
                }else{
                    //Let user know that their password or group name was too short.
                    Toast.makeText(NewGroupActivity.this, "New group name or password is too short", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
