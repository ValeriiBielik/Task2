package com.my.bielik.task2.api.response.object;

import java.util.List;

public class PhotoList {

    private int page;
    private int pages;
    private int perpage;
    private int total;

    private List<ResponsePhotoItem> photo;

    public int getPages() {
        return pages;
    }

    public List<ResponsePhotoItem> getPhoto() {
        return photo;
    }
}
