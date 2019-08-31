package com.my.bielik.task2;

import android.graphics.Bitmap;

public class Image {

    private Bitmap bitmap;
    private String source;

    public Image(String source, Bitmap bitmap) {
        this.source = source;
        this.bitmap = bitmap;
    }

    public Bitmap getImage() {
        return bitmap;
    }

    public String getSource() {
        return source;
    }
}
