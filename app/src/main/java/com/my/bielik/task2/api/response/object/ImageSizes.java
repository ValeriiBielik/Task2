package com.my.bielik.task2.api.response.object;

import java.util.List;

public class ImageSizes {
    private int canblog;
    private int canprint;
    private int candonwload;

    private List<ImageResponse> size;

    public int getCanblog() {
        return canblog;
    }

    public int getCanprint() {
        return canprint;
    }

    public int getCandonwload() {
        return candonwload;
    }

    public List<ImageResponse> getSize() {
        return size;
    }
}
