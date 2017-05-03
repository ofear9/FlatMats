package com.example.ranendelman.flatmates.Activities;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.ranendelman.flatmates.Holders.ListHolder;
import com.example.ranendelman.flatmates.Holders.UsersListHolder;
import com.example.ranendelman.flatmates.Fragments.SupermarketListFragment;
import com.example.ranendelman.flatmates.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class SuperMarketListActivity extends AppCompatActivity {

    private static Button addNewItem;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String itemName;
    final static String BUNDLE_KEY = "uid";
    private String fbID;
    private final ListHolder listHolder = new ListHolder(null);
    int position = 0;
    private SupermarketListFragment fragment = null;
    private UsersListHolder usersListHolder;
    private int homeID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_super_market_list);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        addNewItem = (Button) findViewById(R.id.addNewItem);

        setupClearAllButton();

        listHolder.list = getItemsList();

        displayView(position);
    }

    private void displayView(int position) {

        if (position == 0) {
            Bundle bundle = new Bundle();
            bundle.putString(BUNDLE_KEY, getUserID());
            fragment = new SupermarketListFragment();
            fragment.setArguments(bundle);
            addNewItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v(" Add New Item", "Add New Item");
                    AlertDialog.Builder builder = new AlertDialog.Builder(SuperMarketListActivity.this);
                    builder.setTitle("Enter the new Item details");
                    LinearLayout layout = new LinearLayout(SuperMarketListActivity.this);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    final EditText name = new EditText(SuperMarketListActivity.this);
                    name.setHint("Item name");
                    layout.addView(name);

                    final EditText amount = new EditText(SuperMarketListActivity.this);
                    amount.setHint("Quantity");
                    layout.addView(amount);

                    name.setInputType(InputType.TYPE_CLASS_TEXT);
                    amount.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(layout);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            itemName = name.getText().toString();
                            if (itemName != null && !itemName.equals(""))
                                insertNewItem(itemName, amount.getText().toString());
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
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
        }
    }

    protected ArrayList<String> getItemsList() {
        final String user = getUserID();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child("homes").child("" + getUserHomeID(user)).hasChild("Supermarket list"))
                    listHolder.list = (ArrayList<String>) snapshot.child("homes").child("" + getUserHomeID(user)).child("Supermarket list").getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return listHolder.list;
    }

    protected void insertNewItem(String item, String amount) {
        ArrayList<String> sList;
        final String user = getUserID();
        sList = fragment.getListHolder().list;
        if (sList == null)
            sList = new ArrayList<>();
        sList.add(amount + " " + item);
        mDatabase.child("homes").child("" + getUserHomeID(user)).child("Supermarket list").setValue(sList);
        fragment.getList(getUserID());
        Log.v(" NEW ITEM INSERTED", "NEW ITEM INSERTED TO DATA BASE");
    }

    private void extractDataFromBundle() {
        Intent intent = getIntent();

        Bundle bundle = intent.getBundleExtra(LogInActivity.BUNDLE_KEY);

        fbID = bundle.getString(LogInActivity.BUNDLE_KEY);
    }

    public String getUserID() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null)
            return mAuth.getCurrentUser().getUid();
        else {
            extractDataFromBundle();
            return fbID;
        }
    }

    protected void setupClearAllButton() {

        final String user = getUserID();

        Button clearAll = (Button) findViewById(R.id.clearAll);

        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("homes").child("" + getUserHomeID(user)).child("Supermarket list").setValue(null);
                fragment.getList(user);
            }
        });
    }

    protected int getUserHomeID(final String userID) {
        usersListHolder = new UsersListHolder(null);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("users")) {
                    usersListHolder.list = (ArrayList<HashMap>) snapshot.child("users").getValue();
                    for (HashMap hashMap : usersListHolder.list)
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

    public int getHomeID() {
        return homeID;
    }

    public void setHomeID(int homeID) {
        this.homeID = homeID;
    }
}
