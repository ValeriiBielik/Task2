package com.my.bielik.task2.response_objects;

public class FlickrResponse {

    public static final String STAT_OK = "ok";

    private String stat;
    private PhotoList photos;

    public String getStat() {
        return stat;
    }

    public PhotoList getPhotos() {
        return photos;
    }
}
