package com.my.bielik.task2.database.object;

public class PhotoItem {

    private String searchText;
    private String url;
    private int userId;
    private String photoId;

    public PhotoItem(String searchText, String url, int userId, String photoId) {
        this.searchText = searchText == null ? "" : searchText;
        this.userId = userId;
        this.url = url;
        this.photoId = photoId;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getSearchText() {
        return searchText;
    }

    public String getUrl() {
        return url;
    }

    public int getUserId() {
        return userId;
    }

    public String getPhotoId() {
        return photoId;
    }
}
