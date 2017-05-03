package com.example.ranendelman.flatmates.Classes;

/**
 * Created by RanEndelman on 25/03/2017.
 */

public class Fault {
    private String title;
    private String description;
    private String date;

    public Fault(String title, String description, String date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
