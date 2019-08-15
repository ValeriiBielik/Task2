package com.my.bielik.task2.database.object;

public class PhotoItem {

    private String searchText;
    private String url;
    private int userId;

    public PhotoItem(String searchText, String url, int userId) {
        this.searchText = searchText;
        this.userId = userId;
        this.url = url;
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
}
