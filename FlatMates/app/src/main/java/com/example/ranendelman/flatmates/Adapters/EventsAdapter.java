package com.example.ranendelman.flatmates.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ranendelman.flatmates.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ofear on 3/30/2017.
 */

public class EventsAdapter extends ArrayAdapter<HashMap> {
    private Activity context;
    private ArrayList<HashMap> list = new ArrayList<>();

    public EventsAdapter(Activity context, ArrayList<HashMap> list) {
        super(context, R.layout.evant_item, list);
        this.context = context;
        this.list = list;
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.evant_item, null, true);

        TextView date = (TextView) listViewItem.findViewById(R.id.date);
        TextView title = (TextView) listViewItem.findViewById(R.id.title);
        TextView details = (TextView) listViewItem.findViewById(R.id.details);

        HashMap<String, String> event = list.get(position);

        date.setText(event.get("Date"));
        title.setText(event.get("Title"));
        details.setText(event.get("Details"));


        return listViewItem;
    }

}
