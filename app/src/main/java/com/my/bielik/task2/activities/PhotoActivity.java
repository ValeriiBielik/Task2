package com.my.bielik.task2.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.my.bielik.task2.R;
import com.my.bielik.task2.databases.PhotosDBHelper;

import static com.my.bielik.task2.activities.LoginActivity.USER_ID_EXTRA;

public class PhotoActivity extends AppCompatActivity {

    public static final String URL_EXTRA = "url";
    public static final String SEARCH_TEXT_EXTRA = "search_text";

    private TextView tvSearchInfo;
    private WebView webView;

    private String url;
    private String searchText;
    private int userId;

    private PhotosDBHelper photosDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        tvSearchInfo = findViewById(R.id.tv_search_info);
        webView = findViewById(R.id.web_view);

        if (getIntent() != null) {
            url = getIntent().getStringExtra(URL_EXTRA);
            searchText = getIntent().getStringExtra(SEARCH_TEXT_EXTRA);
            userId = getIntent().getIntExtra(USER_ID_EXTRA, 0);
        }

        photosDBHelper = new PhotosDBHelper(this);

        tvSearchInfo.setText(searchText);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return !URLUtil.isNetworkUrl(url);
            }
        });
        webView.loadUrl(url);

        addToRecent();
    }

    public void addToFavourites(View view) {
        boolean success = photosDBHelper.addFavourite(searchText, url, userId);

        if (success) {
            Toast.makeText(this, getString(R.string.toast_added_to_favourites), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.toast_in_favourites), Toast.LENGTH_SHORT).show();
        }
    }

    public void removeFromFavourites(View view) {
        boolean success = photosDBHelper.removeFavourite(url, userId);

        if (success) {
            Toast.makeText(this, getString(R.string.toast_deleted_from_favourites), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.toast_not_in_favourites), Toast.LENGTH_SHORT).show();
        }
    }

    public void addToRecent() {
        photosDBHelper.addRecent(url, userId);
    }
}