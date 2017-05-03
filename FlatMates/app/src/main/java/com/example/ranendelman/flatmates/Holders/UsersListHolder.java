package com.example.ranendelman.flatmates.Holders;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by RanEndelman on 30/03/2017.
 */

public class UsersListHolder {
    public ArrayList<HashMap> list;

    public UsersListHolder(ArrayList<HashMap> list) {
        this.list = list;
    }

    public ArrayList<HashMap> getList() {
        return list;
    }

    public void setList(ArrayList<HashMap> list) {
        this.list = list;
    }
}
