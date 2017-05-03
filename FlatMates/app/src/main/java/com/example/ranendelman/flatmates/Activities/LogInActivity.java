package com.example.ranendelman.flatmates.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ranendelman.flatmates.Holders.UsersListHolder;
import com.example.ranendelman.flatmates.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.facebook.FacebookSdk;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by RanEndelman on 03/02/2017.
 */

public class LogInActivity extends AppCompatActivity {

    final public static String BUNDLE_KEY = "signUp";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CallbackManager callbackManager;
    private DatabaseReference mDatabase;
    private UsersListHolder listHolder;
    private LoginButton loginButton;
    private boolean loggedIn;
    private String fbID;
    private final FBHolder fbHolder = new FBHolder(null, null);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_log_in);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        setTitle();

        setupFacebookLogIn();

        setupSignInWithFbButton();

        authenticator();

        loggedIn();

        setupSignInButton();

        setupSignUpButton();

        setupSignOutButton();

        setupHomeButton();

    }


    protected void setupSignUpButton() {

        Button b = (Button) findViewById(R.id.signUpButton);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    protected void setupSignInButton() {

        Button b = (Button) findViewById(R.id.signInButton);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!loggedIn) {
                    EditText userNameEditText = (EditText) findViewById(R.id.userNameEditText);

                    String userName = userNameEditText.getText().toString();

                    EditText userNamePassword = (EditText) findViewById(R.id.passwordEditText);

                    String userPassword = userNamePassword.getText().toString();
                    Log.d(" user name: " + userName, "  Password: " + userPassword);
                    if (userName != null && userPassword != null && !userName.equals("") && !userPassword.equals(""))
                        signIn(userName, userPassword);
                    else
                        Toast.makeText(LogInActivity.this, getString(R.string.invalid_email_or_password),
                                Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void setupSignOutButton() {

        Button b = (Button) findViewById(R.id.signOutButton);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loggedIn) {
                    logOut();
                }
            }
        });
    }

    protected void setupHomeButton() {

        Button b = (Button) findViewById(R.id.homeButton);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUserHomeScreen();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        loggedIn();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loggedIn();
    }

    protected void signIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("", "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            Toast.makeText(LogInActivity.this, getString(R.string.sign_in_succeeded),
                                    Toast.LENGTH_SHORT).show();
                            Log.v("", "SIGN IN SUCCEEDED");
                            /** PROCEED TO NEXT ACTIVITY */
                            loggedIn = true;
                            loggedIn();
                            goToUserHomeScreen();

                        } else {
                            Log.w("", "signInWithEmail", task.getException());
                            Toast.makeText(LogInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    protected void setupFacebookLogIn() {
        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);
    }

    protected void setupSignInWithFbButton() {

        Button b = (Button) findViewById(R.id.login_button);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithFaceBook();
            }
        });
    }

    protected void signInWithFaceBook() {
        Profile fbProfile = Profile.getCurrentProfile();

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    logOut();
                }
            }
        };
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loggedIn = true;
                loggedIn();
//                Profile profile = Profile.getCurrentProfile();
//                fbHolder.setID(profile.getId());
//                fbHolder.setName(profile.getName());
//                insertNewFBUserToDB(fbHolder);
                goToUserHomeScreen();

            }

            @Override
            public void onCancel() {
                Toast.makeText(LogInActivity.this, "Authentication canceled.",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(LogInActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    protected void authenticator() {
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    protected void signUp() {
        /**Create new intent for the SignUp Activity*/
        Intent intent = new Intent(this, SignUpActivity.class);

        Bundle bundle = new Bundle();
        /**Insert all the necessary data to the bundle */

        intent.putExtra(BUNDLE_KEY, bundle);

        startActivity(intent);
    }

    protected void goToUserHomeScreen() {
        /**Create new intent for the SignUp Activity*/
        Intent intent = new Intent(this, UserHomeScreenActivity.class);

        Bundle bundle = new Bundle();

        /**Insert all the necessary data to the bundle */
        bundle.putString(BUNDLE_KEY, fbID);

        intent.putExtra(BUNDLE_KEY, bundle);

        startActivity(intent);
    }

    protected void loggedIn() {

        Profile profile = Profile.getCurrentProfile();

        EditText userNameEditText = (EditText) findViewById(R.id.userNameEditText);

        EditText userNamePassword = (EditText) findViewById(R.id.passwordEditText);

        Button sib = (Button) findViewById(R.id.signInButton);

        Button sup = (Button) findViewById(R.id.signUpButton);

        Button fsin = (Button) findViewById(R.id.login_button);

        Button logOut = (Button) findViewById(R.id.signOutButton);

        Button home = (Button) findViewById(R.id.homeButton);

        logOut.setVisibility(View.INVISIBLE);

        home.setVisibility(View.INVISIBLE);

        TextView loggedInAs = (TextView) findViewById(R.id.loggedInAs);

        TextView or = (TextView) findViewById(R.id.or);

        TextView dont = (TextView) findViewById(R.id.dontHave);
        if (profile != null) {
            fbID = profile.getId();

            fbHolder.setID(fbID);

            fbHolder.setName(profile.getName());

            insertNewFBUserToDB(fbHolder);

            userNameEditText.setVisibility(View.GONE);

            userNamePassword.setVisibility(View.GONE);

            sib.setVisibility(View.GONE);

            sup.setVisibility(View.GONE);

            logOut.setVisibility(View.GONE);

            or.setVisibility(View.GONE);

            dont.setVisibility(View.GONE);

            home.setVisibility(View.VISIBLE);

            loggedIn = true;

            loggedInAs.setText("Logged in with Facebook profile: " + profile.getName());
        } else if (mAuth.getInstance().getCurrentUser() != null) {
            userNameEditText.setVisibility(View.GONE);

            userNamePassword.setVisibility(View.GONE);

            fsin.setVisibility(View.GONE);

            sib.setVisibility(View.GONE);

            dont.setVisibility(View.GONE);

            sup.setVisibility(View.GONE);

            logOut.setVisibility(View.VISIBLE);

            or.setVisibility(View.GONE);

            home.setVisibility(View.VISIBLE);

            loggedIn = true;

            loggedInAs.setText("Logged in as: " + mAuth.getInstance().getCurrentUser().getEmail());
        }
    }

    protected void logOut() {
        mAuth.signOut();

        EditText userNameEditText = (EditText) findViewById(R.id.userNameEditText);

        EditText userNamePassword = (EditText) findViewById(R.id.passwordEditText);

        TextView loggedInAs = (TextView) findViewById(R.id.loggedInAs);

        Button sib = (Button) findViewById(R.id.signInButton);

        Button logOut = (Button) findViewById(R.id.signOutButton);

        TextView or = (TextView) findViewById(R.id.or);

        Button sup = (Button) findViewById(R.id.signUpButton);

        TextView dont = (TextView) findViewById(R.id.dontHave);

        Button fsin = (Button) findViewById(R.id.login_button);

        Button home = (Button) findViewById(R.id.homeButton);

        userNameEditText.setVisibility(View.VISIBLE);

        home.setVisibility(View.INVISIBLE);

        userNamePassword.setVisibility(View.VISIBLE);

        fsin.setVisibility(View.VISIBLE);

        sib.setVisibility(View.VISIBLE);

        sib.setVisibility(View.VISIBLE);

        sup.setVisibility(View.VISIBLE);

        or.setVisibility(View.VISIBLE);

        logOut.setVisibility(View.GONE);

        dont.setVisibility(View.VISIBLE);

        loggedIn = false;

        Toast.makeText(LogInActivity.this, getString(R.string.logged_out),
                Toast.LENGTH_SHORT).show();

        loggedInAs.setText("");
    }

    protected void setTitle() {
        ImageView title = (ImageView) findViewById(R.id.title);
        /*title.setShadowLayer(1, 0, 0, Color.BLACK);
        title.setTextSize(50);
        title.setTextColor(Color.GRAY);*/
    }

    protected void insertNewFBUserToDB(final FBHolder fbHolder) {
        listHolder = new UsersListHolder(null);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("users")) {
                    listHolder.list = (ArrayList<HashMap>) snapshot.child("users").getValue();
                    boolean flag = false;
                    for (int i = 0; i < listHolder.list.size(); i++) {
                        if (listHolder.list.get(i).get(fbHolder.getID()) != null) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        HashMap hashMapToInsert = new HashMap();
                        hashMapToInsert.put(fbHolder.getID(), fbHolder.getName());
                        listHolder.list.add(hashMapToInsert);
                        mDatabase.child("users").setValue(listHolder.list);
                    }

                } else {
                    ArrayList<HashMap> users = new ArrayList<>();
                    HashMap hashMapToInsert = new HashMap();
                    hashMapToInsert.put(fbHolder.getID(), fbHolder.getName());
                    users.add(hashMapToInsert);
                    mDatabase.child("users").setValue(listHolder.list);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    class FBHolder {
        private String ID;
        private String name;

        public FBHolder(String ID, String name) {
            this.ID = ID;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }
    }
}

