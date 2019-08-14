package com.my.bielik.task2.retro.response.object;

import java.util.List;

public class PhotoList {

    private int page;
    private int pages;
    private int perpage;
    private int total;
    private List<ResponsePhotoItem> photo;

    public List<ResponsePhotoItem> getPhoto() {
        return photo;
    }
}
