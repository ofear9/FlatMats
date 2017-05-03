package com.example.ranendelman.flatmates.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.ranendelman.flatmates.Adapters.EventsAdapter;
import com.example.ranendelman.flatmates.Classes.Event;
import com.example.ranendelman.flatmates.Holders.EventListHolder;
import com.example.ranendelman.flatmates.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class EventsActivity extends AppCompatActivity {
    private static Button addNewEventBt;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String fbID;
    private ListView mListView;
    private EventListHolder mList = new EventListHolder(null);
    private EventsAdapter adapter;
    private int homeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evants);
        extractDataFromBundle();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setupListView();
        setupNewEventButton();
        mList.list = getItemsList();

    }

    public void setupNewEventButton() {
        addNewEventBt = (Button) findViewById(R.id.addNewEvent);
        addNewEventBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupNewEvent();
            }
        });
    }


    public void setupListView() {
        mListView = (ListView) findViewById(R.id.event_list);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EventsActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Do you want to remove this event ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HashMap<String, String> event = mList.list.get(position);
                        Log.v("" + position, "" + position);
                        adapter.remove(event);
                        mDatabase.child("homes").child("" + getHomeID()).child("Events-List").setValue(mList.list);
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                builder.show();
                return true;
            }
        });


    }


    public void setupNewEvent() {
        Log.v(" Add New Item", "Add New Item");
        AlertDialog.Builder builder = new AlertDialog.Builder(EventsActivity.this);
        builder.setTitle("Enter new event");
        LinearLayout layout = new LinearLayout(EventsActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);


        final EditText date = new EditText(EventsActivity.this);
        date.setHint("Enter the date - dd/MM/yyyy HH:mm");
        layout.addView(date);

        final EditText title = new EditText(EventsActivity.this);
        title.setHint("Title");
        layout.addView(title);


        final EditText description = new EditText(EventsActivity.this);
        description.setHint("Description");
        layout.addView(description);

        date.setInputType(InputType.TYPE_CLASS_TEXT);
        title.setInputType(InputType.TYPE_CLASS_TEXT);
        description.setInputType(InputType.TYPE_CLASS_TEXT);

        builder.setView(layout);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if( !title.getText().toString().equals("") &&  !description.getText().toString().equals("") && !date.getText().toString().equals("") ) {
                    final Event event = new Event(title.getText().toString(), description.getText().toString(), date.getText().toString());
                    addNewEvent(event);
                }
            }
        });
        builder.show();
    }


    public void addNewEvent(Event event) {

        if (mList.list == null)
            mList.list = new ArrayList<>();
        HashMap hashMap = new HashMap();
        hashMap.put("Date", event.getDateTime());
        hashMap.put("Title", event.getTitle());
        hashMap.put("Details", event.getDetails());

        mList.list.add(hashMap);
        mDatabase.child("homes").child("" + getHomeID()).child("Events-List").setValue(mList.list);

    }

    protected ArrayList<HashMap> getItemsList() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child("homes").child("" + getHomeID()).hasChild("Events-List")) {
                    mList.list = (ArrayList<HashMap>) snapshot.child("homes").child("" + getHomeID()).child("Events-List").getValue();
                    Log.v(" LIST FOUND", "LIST FOUND");
                    adapter = new EventsAdapter(EventsActivity.this, mList.list);
                    mListView.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return mList.list;
    }

    private void extractDataFromBundle() {
        Intent intent = getIntent();

        Bundle bundle = intent.getBundleExtra(LogInActivity.BUNDLE_KEY);

        setHomeID(bundle.getInt(LogInActivity.BUNDLE_KEY));

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

    protected int getHomeID() {
        return homeID;
    }

    protected void setHomeID(int homeID) {
        this.homeID = homeID;
    }

}
