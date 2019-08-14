package com.my.bielik.task2.retro.response;

import com.my.bielik.task2.retro.response.object.PhotoList;

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
