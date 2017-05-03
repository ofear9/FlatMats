package com.example.ranendelman.flatmates.Holders;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by RanEndelman on 25/03/2017.
 */

public class ReceiptsListHolder {
    public ArrayList<HashMap> list;

    public ReceiptsListHolder(ArrayList<HashMap> list) {
        this.list = list;
    }

    public ArrayList<HashMap> getList() {
        return list;
    }

    public void setList(ArrayList<HashMap> list) {
        this.list = list;
    }
}
