package com.my.bielik.task2.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.my.bielik.task2.R;
import com.my.bielik.task2.database.PhotosDBHelper;
import com.my.bielik.task2.database.object.PhotoItem;

import static com.my.bielik.task2.activity.LoginActivity.*;

public class PhotoActivity extends AppCompatActivity {

    private TextView tvSearchInfo;
    private WebView webView;

    private PhotoItem photoItem;

    private PhotosDBHelper photosDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        tvSearchInfo = findViewById(R.id.tv_search_info);
        webView = findViewById(R.id.web_view);

        if (getIntent() != null) {
            photoItem = new PhotoItem(getIntent().getStringExtra(SEARCH_TEXT_EXTRA),
                    getIntent().getStringExtra(URL_EXTRA),
                    getIntent().getIntExtra(USER_ID_EXTRA, 0));
        }

        photosDBHelper = new PhotosDBHelper(this);

        tvSearchInfo.setText(photoItem.getSearchText());
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return !URLUtil.isNetworkUrl(url);
            }
        });
        webView.loadUrl(photoItem.getUrl());

        addToRecent();
    }

    public void addToFavourites(View view) {
        String response = photosDBHelper.addFavourite(photoItem)
                ? getString(R.string.toast_added_to_favourites) : getString(R.string.toast_in_favourites);
        Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
    }

    public void removeFromFavourites(View view) {
        String response = photosDBHelper.removeFavourite(photoItem)
                ? getString(R.string.toast_deleted_from_favourites) : getString(R.string.toast_not_in_favourites);
        Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
    }

    public void addToRecent() {
        photosDBHelper.addToRecent(photoItem);
    }
}