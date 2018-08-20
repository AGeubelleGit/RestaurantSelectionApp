package edu.illinois.finalproject.grouplist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

//Group detail activity shows information about a specific group and allows the creator to delete it.
public class GroupDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if(extras == null) {
            finish();
        }
        //Get group info from the intent.
        final String groupName = extras.getString("GroupName");
        final String groupId = extras.getString("GroupId");
        final String groupKey = extras.getString("GroupKey");
        final String groupCreator = extras.getString("GroupCreator");

        //Instantiate views.
        final TextView groupNameTextView = (TextView) findViewById(R.id.group_detail_title_text);
        final Button removeButton = (Button) findViewById(R.id.group_detail_remove_button);
        final Button cancelButton = (Button) findViewById(R.id.group_detail_cancel_button);

        //Set the activity title to the gorup name.
        groupNameTextView.setText(groupName);

        //Return to the group list activity when cancel button is pressed.
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupDetailActivity.this, GroupListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Get the id of the current firebase user.
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String userId = auth.getCurrentUser().getUid();

                //If the current user is not the same as the creator,
                //send message to user that only creator can remove the group.
                if(!userId.equals(groupCreator)) {
                    Toast.makeText(GroupDetailActivity.this, "Only group creator can remove groups.", Toast.LENGTH_LONG).show();
                    return;
                }

                //Access the database and remove all references to the current groups:
                //This includes the group under the root reference and the group in the list of groups.
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference rootReference = database.getReference("");
                final DatabaseReference groupsReference = database.getReference("Groups");
                groupsReference.child(groupKey).setValue(null);
                rootReference.child(groupId).setValue(null);

                //Return to the group list activity.
                Intent intent = new Intent(GroupDetailActivity.this, GroupListActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

}
