package edu.illinois.finalproject.grouplist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.acl.Group;

import edu.illinois.finalproject.MainActivity;
import edu.illinois.finalproject.R;

/**
 * Created by alexandregeubelle on 12/7/17.
 */

public class FirebaseGroupViewHolder extends RecyclerView.ViewHolder {
    private TextView groupNameTextView;
    private Button selectButton;
    private View itemView;

    //Constructor for View Holder to display all restaurant groups from firebase database.
    public FirebaseGroupViewHolder(View itemView) {
        super(itemView);

        this.itemView = itemView;
        this.groupNameTextView = (TextView) itemView.findViewById(R.id.group_cell_group_name_text);
        this.selectButton = (Button) itemView.findViewById(R.id.group_cell_select_button);
    }

    void bind(final GroupInfo group) {
        //Set the text view to the name of the group.
        groupNameTextView.setText(group.getName());

        //When the select button is pressed, ask user for password.
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordAlert(v.getContext(), group);

            }
        });

        //If any other part of the cell is pressed, display the detail activity for the group.
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get values from the group.
                String firebaseId = group.getId();
                String groupName = group.getName();
                String groupKey = group.getKey();
                String groupCreator = group.getCreator();

                //Start intent to pass to the group detail activity.
                final Context context = view.getContext();
                Intent detailedIntent = new Intent(context, GroupDetailActivity.class);
                detailedIntent.putExtra("GroupName", groupName);
                detailedIntent.putExtra("GroupId", firebaseId);
                detailedIntent.putExtra("GroupKey", groupKey);
                detailedIntent.putExtra("GroupCreator", groupCreator);
                context.startActivity(detailedIntent);
            }
        });
    }

    //Alert Dialog documentation: https://developer.android.com/reference/android/app/AlertDialog.html
    private void showPasswordAlert(final Context context, final GroupInfo groupInfo) {
        //Create passwordDialog
        AlertDialog.Builder passwordDialog = new AlertDialog.Builder(context);

        final String title = groupInfo.getName();
        final String message = "Enter password";
        final String positiveButtonText = "Login";
        final String negativeButtonText = "Cancel";

        //Set the title and message of the alert dialog
        passwordDialog.setTitle(title);
        passwordDialog.setMessage(message);

        //Add an edit text for the user to input their password.
        final EditText passwordBox = new EditText(context);
        passwordBox.setHint("password");
        //Set the edit text to show * instead of letters.
        //Code from this stack overflow: https://stackoverflow.com/questions/6094962/android-how-to-set-password-property-in-an-edit-text/6095083
        passwordBox.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordBox.setTransformationMethod(PasswordTransformationMethod.getInstance());
        passwordDialog.setView(passwordBox);

        passwordDialog.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String firebaseId = groupInfo.getId();
                String groupName = groupInfo.getName();
                String groupPass = groupInfo.getPassword();
                String password = passwordBox.getText().toString().trim();

                //If the group does not have a password, or the password was input correctly,
                //move onto the next activity to display this group.
                if (groupPass == null || password.equals(groupPass)) {
                    Intent detailedIntent = new Intent(context, MainActivity.class);
                    detailedIntent.putExtra("GroupName", groupName);
                    detailedIntent.putExtra("GroupId", firebaseId);
                    context.startActivity(detailedIntent);
                }else{
                    Toast.makeText(context, "Password Incorrect", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Remove the dialog if they press cancel.
        passwordDialog.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        passwordDialog.show();

    }
}
