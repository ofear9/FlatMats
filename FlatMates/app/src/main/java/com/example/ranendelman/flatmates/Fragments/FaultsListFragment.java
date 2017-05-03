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

import com.example.ranendelman.flatmates.Activities.LogInActivity;
import com.example.ranendelman.flatmates.Adapters.FaultsListAdapter;
import com.example.ranendelman.flatmates.Holders.FaultsListHolder;
import com.example.ranendelman.flatmates.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by RanEndelman on 25/03/2017.
 */

public class FaultsListFragment extends Fragment {

    private FaultsListAdapter adapter;
    final static String BUNDLE_KEY = "uid";
    private ListView mListView;
    private DatabaseReference mDatabase;

    public FaultsListHolder getListHolder() {
        return listHolder;
    }

    private final FaultsListHolder listHolder = new FaultsListHolder(null);
    private String UID;
    private int homeID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            UID = bundle.getString(BUNDLE_KEY);
            homeID = bundle.getInt(LogInActivity.BUNDLE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_supermarket, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mListView = (ListView) rootView.findViewById(R.id.itemsListView);
        getList();

        if (listHolder.list != null) {
            adapter = new FaultsListAdapter(getActivity(), R.layout.fragment_supermarket_list_item, listHolder.list);
            mListView.setAdapter(adapter);
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Fault '" + listHolder.list.get(i).get("title").toString() + "'" + " Description:");
                builder.setMessage(listHolder.list.get(i).get("description").toString());
                LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);
                builder.setView(layout);
                builder.show();
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Are you sure you want to delete the fault " +
                        "'" + listHolder.list.get(position).get("title") + "' ?");
                LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);
                builder.setView(layout);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listHolder.list.remove(position);
                        updateListInDB(listHolder.list);
                        adapter = new FaultsListAdapter(getActivity(), R.layout.fragment_supermarket_list_item, listHolder.list);
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

    public void getList() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child("homes").child("" + homeID).hasChild("Faults list")) {
                    listHolder.list = (ArrayList<HashMap>) snapshot.child("homes").child("" + homeID).child("Faults list").getValue();
                    Log.v(" LIST FOUND", "LIST FOUND");
                    adapter = new FaultsListAdapter(getActivity(), R.layout.fragment_supermarket_list_item, listHolder.list);
                    mListView.setAdapter(adapter);

                } else {
//                    adapter = new AdapterView<>();
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

    public void updateListInDB(ArrayList<HashMap> list) {
        if (list != null)
            mDatabase.child("homes").child("" + homeID).child("Faults list").setValue(list);
        else
            mDatabase.child("homes").child("" + homeID).child("Faults list");
    }
}
