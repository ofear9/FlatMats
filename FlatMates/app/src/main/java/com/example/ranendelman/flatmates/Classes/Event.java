package com.example.ranendelman.flatmates.Classes;

import android.support.annotation.NonNull;

/**
 * Created by ofear on 3/30/2017.
 */
public class Event implements Comparable<Event> {
    private String title;
    private String details;
    private String dateTime;

    public Event(String title, String details, String dateTime) {
        this.title = title;
        this.details = details;
        this.dateTime = dateTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }


    @Override
    public int compareTo(@NonNull Event o) {
        return getDateTime().compareTo(o.getDateTime());
    }

}
