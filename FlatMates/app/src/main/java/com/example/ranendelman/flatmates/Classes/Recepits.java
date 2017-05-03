package com.example.ranendelman.flatmates.Classes;

/**
 * Created by ofear on 3/25/2017.
 */

public class Recepits {
    private String imageUrl;
    private String UID;

    public Recepits(){

    }

    public Recepits(String imageUrl, String UID) {
        this.imageUrl = imageUrl;
        this.UID = UID;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
