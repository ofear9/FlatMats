package com.example.ranendelman.flatmates.Classes;

/**
 * Created by RanEndelman on 06/03/2017.
 */

public class Home {
    private String name;
    private String ID;
    public Home() {

    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
