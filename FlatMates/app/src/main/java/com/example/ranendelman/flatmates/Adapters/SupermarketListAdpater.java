package com.example.ranendelman.flatmates.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.example.ranendelman.flatmates.R;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by RanEndelman on 08/03/2017.
 */

public class SupermarketListAdpater extends ArrayAdapter<String> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<String> list;

    public SupermarketListAdpater(Context context, int layoutResourceId, ArrayList<String> list) {
        super(context, layoutResourceId, list);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.list = list;
    }

    /**
     * This method describe how to show the ArrayList on the screen
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        View row = convertView;
        
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.number = (TextView) row.findViewById(R.id.number);
            holder.name = (TextView) row.findViewById(R.id.name);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.number.setText((position + 1) + ".");
        holder.name.setText(list.get(position));
        return row;

    }

    private class ViewHolder {
        TextView number, name;
    }
}
