package com.my.bielik.task2.api.response.object;

public class PhotoListResponse {

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
