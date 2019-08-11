package com.my.bielik.task2.response_objects;

import java.util.ArrayList;

public class PhotoList {

    private int page;
    private int pages;
    private int perpage;
    private int total;
    private ArrayList<PhotoItem> photo;

    public int getPage() {
        return page;
    }

    public int getPages() {
        return pages;
    }

    public int getPerpage() {
        return perpage;
    }

    public int getTotal() {
        return total;
    }

    public ArrayList<PhotoItem> getPhoto() {
        return photo;
    }
}
