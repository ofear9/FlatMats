package com.example.ranendelman.flatmates.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.ranendelman.flatmates.Adapters.SupermarketListAdpater;
import com.example.ranendelman.flatmates.Holders.ListHolder;
import com.example.ranendelman.flatmates.Holders.UsersListHolder;
import com.example.ranendelman.flatmates.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ofear on 1/7/2017.
 */

public class SupermarketListFragment extends Fragment {

    private SupermarketListAdpater adapter;
    final static String BUNDLE_KEY = "uid";
    private ListView mListView;
    private DatabaseReference mDatabase;

    public ListHolder getListHolder() {
        return listHolder;
    }

    private UsersListHolder usersListHolder = new UsersListHolder(null);
    private int homeID;
    private final ListHolder listHolder = new ListHolder(null);
    private String UID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            UID = bundle.getString(BUNDLE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_supermarket, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mListView = (ListView) rootView.findViewById(R.id.itemsListView);
        getList(UID);

        if (listHolder.list != null) {
            adapter = new SupermarketListAdpater(getActivity(), R.layout.fragment_supermarket_list_item, listHolder.list);
            mListView.setAdapter(adapter);
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (listHolder.list.get(i).contains("✔")) {
                    String s = listHolder.list.get(i).replace("✔", "");
                    listHolder.list.add(s);
                    listHolder.list.remove(i);
                } else {
                    String s = listHolder.list.get(i);
                    listHolder.list.add("✔ " + listHolder.list.get(i));
                    listHolder.list.remove(s);
                }
                updateListInDB(listHolder.list);
                adapter = new SupermarketListAdpater(getActivity(), R.layout.fragment_supermarket_list_item, listHolder.list);
                mListView.setAdapter(adapter);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Are you sure you want to delete " + listHolder.list.get(position) + "?");
                LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);
                builder.setView(layout);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String s = listHolder.list.get(position);
                        listHolder.list.remove(s);
                        updateListInDB(listHolder.list);
                        adapter = new SupermarketListAdpater(getActivity(), R.layout.fragment_supermarket_list_item, listHolder.list);
                        mListView.setAdapter(adapter);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

                return false;
            }
        });
        return rootView;
    }

    public void getList(final String userID) {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            int localHomeID = 0;

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("users")) {
                    usersListHolder.list = (ArrayList<HashMap>) snapshot.child("users").getValue();
                    for (HashMap hashMap : usersListHolder.list)
                        if (hashMap.get(userID) != null) {
                            localHomeID = ((Long) hashMap.get("home ID")).intValue();
                            setHomeID(localHomeID);
                            break;
                        }
                }
                if (snapshot.child("homes").child("" + localHomeID).hasChild("Supermarket list")) {
                    listHolder.list = (ArrayList<String>) snapshot.child("homes").child("" + localHomeID).child("Supermarket list").getValue();
                    Log.v(" LIST FOUND", "LIST FOUND");
                    adapter = new SupermarketListAdpater(getActivity(), R.layout.fragment_supermarket_list_item, listHolder.list);
                    mListView.setAdapter(adapter);

                } else {
                    mListView.setAdapter(null);
                    listHolder.list = null;
                    Log.v(" LIST NOT FOUND", "LIST  NOT FOUND");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }

    public void updateListInDB(ArrayList<String> list) {
        if (list != null)
            mDatabase.child("homes").child("" + getHomeID()).child("Supermarket list").setValue(list);
        else
            mDatabase.child("homes").child("" + getHomeID()).child("Supermarket list");
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

    public void setHomeID(int receivedHomeID) {
        homeID = receivedHomeID;
    }
}