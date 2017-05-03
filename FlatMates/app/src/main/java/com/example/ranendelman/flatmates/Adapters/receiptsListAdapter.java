package com.example.ranendelman.flatmates.Adapters;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ranendelman.flatmates.Classes.Recepits;
import com.example.ranendelman.flatmates.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ofear on 3/27/2017.
 */

public class receiptsListAdapter extends ArrayAdapter<HashMap> {
    private Activity context;
    private ArrayList<HashMap> list = new ArrayList<>();
    private StorageReference mStorage;


    public receiptsListAdapter(Activity context, int layoutResourceId, ArrayList<HashMap> list) {
        super(context,R.layout.item_recepit,list);
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView , ViewGroup parent){

        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.item_recepit,null,true);

        TextView textViewDet = (TextView)listViewItem.findViewById(R.id.text_details);

        final ImageView receImage = (ImageView)listViewItem.findViewById(R.id.recepits_image);

        HashMap<String,String> recepeit = list.get(position);

        textViewDet.setText(recepeit.get("uid"));

        final Uri myUri = Uri.parse(recepeit.get("imageUrl"));

        mStorage = FirebaseStorage.getInstance().getReference();

        final StorageReference filePath = mStorage.
                child(myUri.getLastPathSegment());

        Glide.with(context /*context*/)
                .using(new FirebaseImageLoader())
                .load(filePath).
                 centerCrop()
                .into(receImage);

        return listViewItem;
    }
}
