package com.example.ranendelman.flatmates.Activities;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.ranendelman.flatmates.Holders.FaultsListHolder;
import com.example.ranendelman.flatmates.Fragments.FaultsListFragment;
import com.example.ranendelman.flatmates.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class FaultesActivity extends AppCompatActivity {

    private static Button addNewFault;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String itemName;
    final static String BUNDLE_KEY = "uid";
    final static String BUNDLE_KEY2 = "key";
    private String fbID;
    private final FaultsListHolder listHolder = new FaultsListHolder(null);
    int position = 0;
    private FaultsListFragment fragment = null;
    private int homeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_faultes);

        extractDataFromBundle();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        addNewFault = (Button) findViewById(R.id.addNewFault);

        setupClearAllButton();

        listHolder.list = getItemsList();

        displayView(position);
    }

    private void displayView(int position) {

        if (position == 0) {
            Bundle bundle = new Bundle();
            bundle.putString(BUNDLE_KEY, getUserID());
            bundle.putInt(LogInActivity.BUNDLE_KEY, getHomeID());
            fragment = new FaultsListFragment();
            fragment.setArguments(bundle);
            addNewFault.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v(" Add New Item", "Add New Item");
                    AlertDialog.Builder builder = new AlertDialog.Builder(FaultesActivity.this);
                    builder.setTitle("Enter the new Item details");
                    LinearLayout layout = new LinearLayout(FaultesActivity.this);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    final EditText name = new EditText(FaultesActivity.this);
                    name.setHint("Item name");
                    layout.addView(name);

                    final EditText description = new EditText(FaultesActivity.this);
                    description.setHint("Description");
                    layout.addView(description);

                    name.setInputType(InputType.TYPE_CLASS_TEXT);
                    description.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(layout);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            itemName = name.getText().toString();
                            if (itemName != null && !itemName.equals(""))
                                insertNewItem(itemName, description.getText().toString());
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

    protected ArrayList<HashMap> getItemsList() {
        final String user = getUserID();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child("homes").child("" + getHomeID()).hasChild("Faults list"))
                    listHolder.list = (ArrayList<HashMap>)snapshot.child("homes").child("" + getHomeID()).child("Faults list").getValue();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return listHolder.list;
    }

    protected void insertNewItem(String item, String description) {
        ArrayList<HashMap> sList;
        sList = fragment.getListHolder().list;
        if(sList == null)
            sList = new ArrayList<>();
        HashMap hashMap = new HashMap();
        hashMap.put("title", item);
        hashMap.put("description", description);
        hashMap.put("date", new SimpleDateFormat("yyyy.MM.dd").format(new Date()));
        sList.add(hashMap);
        mDatabase.child("homes").child("" + getHomeID()).child("Faults list").setValue(sList);
        fragment.getList();
        Log.v(" NEW ITEM INSERTED", "NEW ITEM INSERTED TO DATA BASE");
    }

    private void extractDataFromBundle() {
        Intent intent = getIntent();

        Bundle bundle = intent.getBundleExtra(BUNDLE_KEY2);

        setHomeID(bundle.getInt(LogInActivity.BUNDLE_KEY));

        fbID = bundle.getString(BUNDLE_KEY2);
    }

    public String getUserID() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null)
            return mAuth.getCurrentUser().getUid();
        else  {
            extractDataFromBundle();
            return fbID;
        }
    }

    protected void setupClearAllButton() {

        Button clearAll = (Button) findViewById(R.id.clearAll);

        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("homes").child("" + getHomeID()).child("Faults list").setValue(null);
                fragment.getList();
            }
        });
    }

    protected int getHomeID() {
        return homeID;
    }

    protected void setHomeID(int homeID) {
        this.homeID = homeID;
    }
}
