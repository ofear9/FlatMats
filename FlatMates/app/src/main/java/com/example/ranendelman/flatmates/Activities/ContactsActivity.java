package com.example.ranendelman.flatmates.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ranendelman.flatmates.Adapters.ContactAdapter;
import com.example.ranendelman.flatmates.Classes.Contact;
import com.example.ranendelman.flatmates.Holders.EventListHolder;
import com.example.ranendelman.flatmates.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class ContactsActivity extends AppCompatActivity {
    private static Button addNewEventBt;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String fbID;
    private ListView mListView;
    private EventListHolder mList = new EventListHolder(null);
    private ContactAdapter adapter;
    private int homeID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        extractDataFromBundle();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setupListView();
        setupNewEventButton();
        mList.list = getItemsList();
        getPhoneCallReq();

    }

    private void getPhoneCallReq(){
        if (checkSelfPermission(Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                    1);

            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(ContactsActivity.this,"Phone Permissions granted " ,
                            Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(ContactsActivity.this,"Phone Permissions denied " ,
                            Toast.LENGTH_SHORT).show();

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    private void setupListView() {
        mListView = (ListView) findViewById(R.id.contact_list);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ContactsActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Do you want remove this number ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HashMap<String, String> contact = mList.list.get(position);
                        Log.v("" + position, "" + position);
                        adapter.remove(contact);
                        Collections.sort(mList.list, (Comparator<? super HashMap>) mapComparator);
                        mDatabase.child("homes").child("" + getHomeID()).child("Contact-List").setValue(mList.list);
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

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ContactsActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Do you want call this number ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HashMap<String, String> contact = mList.list.get(position);
                        Log.v("" + position, "" + position);
                         String num = contact.get("Number");
                         performDial(num);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                builder.show();
            }
        });

    }

    private void performDial(String numberString) {
        if (!numberString.equals("")) {
            Uri number = Uri.parse("tel:" + numberString);
            Intent dial = new Intent(Intent.ACTION_CALL, number);
            if (checkSelfPermission(Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED)
            startActivity(dial);
            else{
                Toast.makeText(ContactsActivity.this,"Phone Permissions denied " ,
                        Toast.LENGTH_SHORT).show();
            }

        }
    }



    protected ArrayList<HashMap> getItemsList() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child("homes").child("" + getHomeID()).hasChild("Contact-List")) {
                    mList.list = (ArrayList<HashMap>) snapshot.child("homes").child("" + getHomeID()).child("Contact-List").getValue();
                    Log.v(" LIST FOUND", "LIST FOUND");
                    adapter = new ContactAdapter(ContactsActivity.this, mList.list);
                    mListView.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return mList.list;
    }

    public void setupNewEventButton() {
        addNewEventBt = (Button) findViewById(R.id.addNewContact);
        addNewEventBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupNewContact();
            }
        });
    }

    private void setupNewContact() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ContactsActivity.this);
        builder.setTitle("Enter new contact details");
        LinearLayout layout = new LinearLayout(ContactsActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);


        final EditText name = new EditText(ContactsActivity.this);
        name.setHint("Enter contact name");
        layout.addView(name);

        final EditText number = new EditText(ContactsActivity.this);
        number.setHint("Enter contact number");
        layout.addView(number);

        name.setInputType(InputType.TYPE_CLASS_TEXT);
        number.setInputType(InputType.TYPE_CLASS_TEXT);

        builder.setView(layout);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String x = number.getText().toString();
                String y =  name.getText().toString();
                if(!name.getText().toString().equals("") && !number.getText().toString().equals("")) {
                    String outputName = name.getText().toString().substring(0, 1).toUpperCase() + name.getText().toString().substring(1);
                    final Contact contact = new Contact(outputName, number.getText().toString());
                    addNeContact(contact);
                }
                else {
                    Toast.makeText(ContactsActivity.this, "Invalid input",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();

    }

    private void addNeContact(Contact contact) {
        if (mList.list == null)
            mList.list = new ArrayList<>();
        HashMap hashMap = new HashMap();
        hashMap.put("Name", contact.getName());
        hashMap.put("Number", contact.getNumber());

        mList.list.add(hashMap);
        Collections.sort(mList.list, (Comparator<? super HashMap>) mapComparator);

        mDatabase.child("homes").child("" + getHomeID()).child("Contact-List").setValue(mList.list);

    }

    public Comparator<? super HashMap<String, String>> mapComparator = new Comparator<HashMap<String, String>>() {
        public int compare(HashMap<String, String> m1, HashMap<String, String> m2) {
            return m1.get("Name").compareTo(m2.get("Name"));
        }
    };

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

