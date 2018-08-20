package edu.illinois.finalproject.firebaseauthentication;

/**
 * Created by alexandregeubelle on 12/4/17.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import edu.illinois.finalproject.MainActivity;
import edu.illinois.finalproject.R;
import edu.illinois.finalproject.grouplist.GroupListActivity;

//From tutorial on firebase authentication:
// https://www.androidhive.info/2016/06/android-getting-started-firebase-simple-login-registration-auth/
public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        //Instantiate the different views.
        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);


        //When the password reset button is pressed, start a new intent to begin that activity.
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });

        //Close this activity when you press the signIn button to return to previous window.
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Set actions to occur when sign up button is pressed.
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the user input email and password strings.
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                Context context = v.getContext();

                //Check that user input email and password meet required lengths.
                if (TextUtils.isEmpty(email)) {
                    String message = context.getString(R.string.signup_activity_email_empty);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    String message = context.getString(R.string.signup_activity_password_empty);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    String message = context.getString(R.string.signup_activity_password_too_short);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    return;
                }

                //create user in database. Let them know if it succeded through a toast.
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                    Log.d("Auth failed", task.getException().toString());
                                } else {
                                    //If successful go directly to the groupListAcitivity.
                                    startActivity(new Intent(SignupActivity.this, GroupListActivity.class));
                                    finish();
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}