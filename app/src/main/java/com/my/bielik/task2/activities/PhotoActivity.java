package com.my.bielik.task2.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.my.bielik.task2.R;

public class PhotoActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        webView = findViewById(R.id.web_view);

        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return ! URLUtil.isNetworkUrl(url);
            }
        });
        webView.loadUrl(getIntent().getStringExtra("url"));

    }
}
