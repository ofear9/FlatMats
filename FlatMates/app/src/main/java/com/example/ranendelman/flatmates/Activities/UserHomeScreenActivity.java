package com.example.ranendelman.flatmates.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ranendelman.flatmates.Classes.Home;
import com.example.ranendelman.flatmates.Holders.HomeHolder;
import com.example.ranendelman.flatmates.Holders.UserIDHolder;
import com.example.ranendelman.flatmates.Holders.UsersListHolder;
import com.example.ranendelman.flatmates.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by RanEndelman on 03/03/2017.
 */

public class UserHomeScreenActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String BUNDLE_KEY = "key";
    private String fbID;
    private String homeName;
    private String userNameOrMail;
    final private HomeHolder homeHolder = new HomeHolder(null);
    private UsersListHolder listHolder = new UsersListHolder(null);
    private int homeID;
    final UserIDHolder userIDHolder = new UserIDHolder(null);
    static final String BUNDLE_KEY_FB = "fb";
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_screen);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        isHaveHome();
        setupCreateNewHomeButton();
        setupSupermarketButton();
        setupReceiptsButton();
        setupFaultsButton();
        setupAddNewMateButton();
        setupEventsButton();
        setupContactButton();
        permissionRequest();

    }


    private void permissionRequest() {
        if (checkSelfPermission(Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE,Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);

        }

    }

    protected void setupCreateNewHomeButton() {
        Button b = (Button) findViewById(R.id.createNewHOMEButton);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewHomeName();
            }
        });
    }

    protected void setupSupermarketButton() {

        Button b = (Button) findViewById(R.id.supermarketButton);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSupermarketScreen();
            }
        });
    }

    protected void setupReceiptsButton() {
        Button b = (Button) findViewById(R.id.receiptsButton);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToReceiptsScreen();
            }
        });
    }

    protected void setupFaultsButton() {

        Button b = (Button) findViewById(R.id.faultsButton);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFaultsScreen();
            }
        });
    }

    protected void setupAddNewMateButton() {

        Button b = (Button) findViewById(R.id.add_mates);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(UserHomeScreenActivity.this);
                builder.setTitle("Enter the new mate details");
                LinearLayout layout = new LinearLayout(UserHomeScreenActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText name = new EditText(UserHomeScreenActivity.this);
                name.setHint("User email or facebook name");
                layout.addView(name);
                name.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(layout);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userNameOrMail = name.getText().toString();
                        if (userNameOrMail != null && !userNameOrMail.equals("")) {
                            isUserExist(userNameOrMail, getHomeID(), getHomeName());
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
    }

    protected void isHaveHome() {
        final String user;
        user = getUserID();
        listHolder = new UsersListHolder(null);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("users")) {
                    listHolder.list = (ArrayList<HashMap>) snapshot.child("users").getValue();
                    for (HashMap hashMap : listHolder.list)
                        if (hashMap.get(user) != null)
                            if (hashMap.get("home ID") != null) {
                                TextView homeName = (TextView) findViewById(R.id.homeName);
                                homeName.setText(hashMap.get("home name").toString());
                                setHomeID(((Long) hashMap.get("home ID")).intValue());
                                homeName.setShadowLayer(1, 0, 0, Color.BLACK);
                                homeName.setTextSize(50);
                                buttonsVisibility(true);
                                setHomeName(hashMap.get("home name").toString());
                            } else
                                buttonsVisibility(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    protected void buttonsVisibility(boolean isHavingHome) {
        ArrayList<Button> homeButtons = new ArrayList<>();
        homeButtons.add((Button) findViewById(R.id.receiptsButton));
        homeButtons.add((Button) findViewById(R.id.supermarketButton));
        homeButtons.add((Button) findViewById(R.id.faultsButton));
        homeButtons.add((Button) findViewById(R.id.contactsButton));
        homeButtons.add((Button) findViewById(R.id.eventsButton));
        homeButtons.add((Button) findViewById(R.id.add_mates));
        Button createNewHome = (Button) findViewById(R.id.createNewHOMEButton);
        if (isHavingHome) {
            createNewHome.setVisibility(View.INVISIBLE);
            for (Button b : homeButtons)
                b.setVisibility(View.VISIBLE);
        } else {
            for (Button b : homeButtons)
                b.setVisibility(View.INVISIBLE);
            createNewHome.setVisibility(View.VISIBLE);
        }
    }

    protected void createNewHome(final String homeName) {
        Home home = new Home();

        home.setName(homeName);

        homeHolder.setHome(home);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int id = generateHomeId();
                mDatabase.child("homes").child("" + (id)).setValue(homeHolder.getHome());
                insertNewHomeIDToUsersDB(getUserID(), id, homeName);
                buttonsVisibility(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    protected void getNewHomeName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter the new HOME name");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                homeName = input.getText().toString();
                if (homeName != null && !homeName.equals(""))
                    createNewHome(homeName);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    protected String getUserID() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null)
            return mAuth.getCurrentUser().getUid();
        else {
            extractDataFromBundle();
            return fbID;
        }
    }

    private void extractDataFromBundle() {
        Intent intent = getIntent();

        Bundle bundle = intent.getBundleExtra(LogInActivity.BUNDLE_KEY);

        fbID = bundle.getString(LogInActivity.BUNDLE_KEY);
    }

    protected void goToSupermarketScreen() {
        /**Create new intent for the SignUp Activity*/
        Intent intent = new Intent(this, SuperMarketListActivity.class);

        Bundle bundle = new Bundle();
        /**Insert all the necessary data to the bundle */
        bundle.putString(LogInActivity.BUNDLE_KEY, fbID);
        intent.putExtra(LogInActivity.BUNDLE_KEY, bundle);

        startActivity(intent);
    }

    protected void goToFaultsScreen() {
        /**Create new intent for the SignUp Activity*/
        Intent intent = new Intent(this, FaultesActivity.class);

        Bundle bundle = new Bundle();
        /**Insert all the necessary data to the bundle */
        bundle.putString(BUNDLE_KEY, fbID);
        intent.putExtra(BUNDLE_KEY, bundle);
        bundle.putInt(LogInActivity.BUNDLE_KEY, getHomeID());
        startActivity(intent);
    }

    protected void goToReceiptsScreen() {
        /**Create new intent for the Receipts Activity*/
        Intent intent = new Intent(this, ReceiptsActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_FB, fbID);
        bundle.putInt(LogInActivity.BUNDLE_KEY, getHomeID());
        intent.putExtra(LogInActivity.BUNDLE_KEY, bundle);

        startActivity(intent);
    }

    protected void insertNewHomeIDToUsersDB(final String userID, final int homeID, final String homeName) {
        final UsersListHolder listHolder = new UsersListHolder(null);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("users")) {
                    listHolder.list = (ArrayList<HashMap>) snapshot.child("users").getValue();
                    for (HashMap hashMap : listHolder.list)
                        if (hashMap.get(userID) != null) {
                            hashMap.put("home ID", homeID);
                            hashMap.put("home name", homeName);
                            Toast.makeText(UserHomeScreenActivity.this, getString(R.string.mate_added_to_flat),
                                    Toast.LENGTH_SHORT).show();
                            break;
                        }
                    mDatabase.child("users").setValue(listHolder.list);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void setHomeID(int homeID) {
        this.homeID = homeID;
    }

    protected int generateHomeId() {
        Random rand = new Random();
        final int randomNum = rand.nextInt(Integer.MAX_VALUE);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child("homes").hasChild("" + randomNum))
                    generateHomeId();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return randomNum;
    }

    protected void isUserExist(final String userNameOrMail, final int homeID, final String homeName) {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean flag = false;
                if (snapshot.hasChild("users")) {
                    listHolder.list = (ArrayList<HashMap>) snapshot.child("users").getValue();
                    for (HashMap hashMap : listHolder.list) {
                        for (Object v : hashMap.values()) {
                            if (v.equals(userNameOrMail)) {
                                flag = true;
                                getUserIdByMailOrName(userNameOrMail, homeID, homeName);
                                break;
                            }
                            if (flag)
                                break;
                        }
                        if (flag)
                            break;
                    }
                    if (!flag)
                        Toast.makeText(UserHomeScreenActivity.this, getString(R.string.user_not_exist),
                                Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    protected int getUserHomeID(final String userID) {
        listHolder = new UsersListHolder(null);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("users")) {
                    listHolder.list = (ArrayList<HashMap>) snapshot.child("users").getValue();
                    for (HashMap hashMap : listHolder.list)
                        if (hashMap.get(userID) != null) {
                            setHomeID(((Long) hashMap.get("home ID")).intValue());
                            break;
                        }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return homeID;
    }

    protected void getUserIdByMailOrName(final String mailOrName, final int homeID, final String homeName) {
        listHolder = new UsersListHolder(null);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean flag = false;
                if (snapshot.hasChild("users")) {
                    listHolder.list = (ArrayList<HashMap>) snapshot.child("users").getValue();
                    for (HashMap hashMap : listHolder.list) {
                        for (Object key : hashMap.keySet()) {
                            if (mailOrName.equals(hashMap.get(key))) {
                                userIDHolder.setId(key.toString());
                                insertNewHomeIDToUsersDB(key.toString(), homeID, homeName);
                                flag = true;
                                break;
                            }
                            if (flag)
                                break;
                        }
                        if (flag)
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public String getHomeName() {
        return homeName;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public int getHomeID() {
        return homeID;
    }

    protected void setupContactButton() {

        Button b = (Button) findViewById(R.id.contactsButton);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToContactScreen();
            }
        });
    }

    protected void goToContactScreen() {
        /**Create new intent for the Receipts Activity*/
        Intent intent = new Intent(this, ContactsActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString(LogInActivity.BUNDLE_KEY, fbID);
        bundle.putInt(LogInActivity.BUNDLE_KEY, getHomeID());
        intent.putExtra(LogInActivity.BUNDLE_KEY, bundle);

        startActivity(intent);
    }

    private void setupEventsButton() {
        Button b = (Button) findViewById(R.id.eventsButton);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEventsScreen();
            }
        });
    }

    protected void goToEventsScreen() {
        /**Create new intent for the Receipts Activity*/
        Intent intent = new Intent(this, EventsActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString(LogInActivity.BUNDLE_KEY, fbID);
        bundle.putInt(LogInActivity.BUNDLE_KEY, getHomeID());
        intent.putExtra(LogInActivity.BUNDLE_KEY, bundle);

        startActivity(intent);
    }
}
