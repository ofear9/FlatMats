package com.example.ranendelman.flatmates.Holders;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ofear on 3/30/2017.
 */

public class EventListHolder {
    public ArrayList<HashMap> list;

    public EventListHolder(ArrayList<HashMap> list) {
        this.list = list;
    }

    public ArrayList<HashMap> getList() {
        return list;
    }

    public void setList(ArrayList<HashMap> list) {
        this.list = list;
    }
}
