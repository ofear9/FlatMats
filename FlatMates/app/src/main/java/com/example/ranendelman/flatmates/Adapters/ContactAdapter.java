package com.example.ranendelman.flatmates.Adapters;

import android.app.Activity;
import android.graphics.Color;
import android.provider.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.ranendelman.flatmates.R;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by ofear on 3/31/2017.
 */

public class ContactAdapter extends ArrayAdapter<HashMap> {
    private Activity context;
    private ArrayList<HashMap> list = new ArrayList<>();
    private int[] colors = {
            Color.RED, Color.BLUE, Color.GRAY, Color.GREEN,
            Color.CYAN,
    };

    public ContactAdapter(Activity context, ArrayList<HashMap> list) {
        super(context, R.layout.contact_item, list);
        this.context = context;
        this.list = list;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ContactAdapter.ViewHolder holder;
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.contact_item, parent, false);

            holder = new ContactAdapter.ViewHolder();
            holder.Name = (TextView) row.findViewById(R.id.contact_name);
            holder.Number = (TextView) row.findViewById(R.id.contact_number);
            row.setTag(holder);

        } else {
            holder = (ContactAdapter.ViewHolder) row.getTag();
        }


        HashMap<String, String> contact = list.get(position);
        holder.Name.setText(contact.get("Name"));
        holder.Number.setText(contact.get("Number"));

        char first = contact.get("Name").charAt(0);

        TextDrawable drawable = TextDrawable.builder().buildRound((first + "").toUpperCase(),colors[(int)first%5]);

        ImageView image = (ImageView) row.findViewById(R.id.circle_letter);

        image.setImageDrawable(drawable);

        return row;
    }
    private class ViewHolder {
        TextView Name, Number;
    }

}
