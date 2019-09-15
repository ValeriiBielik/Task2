package com.my.bielik.task2.main;

public interface OnPhotoSelectedListener {
    void onPhotoSelected(String title, String url, String photoId);
    void onPhotoFromMemorySelected(String path);
}

