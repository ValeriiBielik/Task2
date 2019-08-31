package com.my.bielik.task2.api.response.object;

public class ImageResponse {

    private String label;
    private int width;
    private int height;
    private String source;
    private String url;
    private String media;

    public String getLabel() {
        return label;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getSource() {
        return source;
    }

    public String getUrl() {
        return url;
    }

    public String getMedia() {
        return media;
    }

    public interface SizesContract {
        String SIZE_THUMNAIL = "Thumbnail";
        String SIZE_LARGE = "Large";
    }
}
