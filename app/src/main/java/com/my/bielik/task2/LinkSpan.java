package com.my.bielik.task2;

import android.content.Context;
import android.content.Intent;
import android.text.style.URLSpan;
import android.view.View;

import com.my.bielik.task2.activities.PhotoActivity;

import static com.my.bielik.task2.activities.LoginActivity.USER_ID_EXTRA;
import static com.my.bielik.task2.activities.PhotoActivity.SEARCH_TEXT_EXTRA;
import static com.my.bielik.task2.activities.PhotoActivity.URL_EXTRA;


public class LinkSpan extends URLSpan {

    private Context context;
    private String searchText;
    private int userId;

    public LinkSpan(Context context, String url, String searchText, int userId) {
        super(url);
        this.context = context;
        this.searchText = searchText;
        this.userId = userId;
    }

    @Override
    public void onClick(View view) {
        String url = getURL();
        if (url != null) {
            Intent intent = new Intent(context, PhotoActivity.class);
            intent.putExtra(URL_EXTRA, url);
            intent.putExtra(SEARCH_TEXT_EXTRA, searchText);
            intent.putExtra(USER_ID_EXTRA, userId);
            context.startActivity(intent);
        }
    }
}

