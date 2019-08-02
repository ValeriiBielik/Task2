package com.my.bielik.task2;

import android.net.Uri;

public class URLManager {

    private static final String API_KEY = "539bab6327cc06a832b5793853bac293";
    private static final String ENDPOINT = "https://api.flickr.com/services/rest/";
    private static final String METHOD_SEARCH = "flickr.photos.search";
    private static final int page = 5;

    private String searchText;

    private static volatile URLManager instance = null;

    private URLManager() {}

    public static URLManager getInstance() {
        if (instance == null) {
            synchronized (URLManager.class) {
                if (instance == null) {
                    instance = new URLManager();
                }
            }
        }
        return instance;
    }

    public void setSearchText(String text){
        searchText = text;
    }

    public String getItemUrl() {
        return Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", METHOD_SEARCH)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .appendQueryParameter("text", searchText)
                .appendQueryParameter("page", String.valueOf(page))
                .build().toString();
    }
}
