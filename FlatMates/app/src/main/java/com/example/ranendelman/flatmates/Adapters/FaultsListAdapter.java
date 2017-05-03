package com.example.ranendelman.flatmates.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.ranendelman.flatmates.Classes.Fault;
import com.example.ranendelman.flatmates.R;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by RanEndelman on 25/03/2017.
 */

public class FaultsListAdapter extends ArrayAdapter<HashMap> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<HashMap> list;

    public FaultsListAdapter(Context context, int layoutResourceId, ArrayList<HashMap> list) {
        super(context, layoutResourceId, list);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.list = list;
    }

    /**
     * This method describe how to show the ArrayList on the screen
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        FaultsListAdapter.ViewHolder holder;
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new FaultsListAdapter.ViewHolder();
            holder.number = (TextView) row.findViewById(R.id.number);
            holder.name = (TextView) row.findViewById(R.id.name);
            holder.date = (TextView) row.findViewById(R.id.date);

            row.setTag(holder);
        } else {
            holder = (FaultsListAdapter.ViewHolder) row.getTag();
        }

        holder.number.setText("");
        holder.name.setText(list.get(position).get("title").toString());
        holder.date.setText(list.get(position).get("date").toString());
        return row;
    }
    private class ViewHolder {
        TextView number, name, date;
    }
}
