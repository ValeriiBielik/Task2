package com.my.bielik.task2.database_objects;

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.text.util.Linkify;

import com.my.bielik.task2.LinkSpan;

public class DatabasePhotoItem {

    private String searchText;
    private StringBuilder urls = new StringBuilder();
    private Context context;

    public DatabasePhotoItem(Context context, String searchText) {
        this.context = context;
        this.searchText = searchText;
    }

    public String getSearchText() {
        return searchText;
    }

    public String getUrl() {
        return urls.toString().trim();
    }

    public Spannable getSpannableUrl() {
        final Spannable result = new SpannableString(Html.fromHtml(urls.toString()));

        Linkify.addLinks(result, Linkify.WEB_URLS);

        URLSpan[] spans = result.getSpans(0, result.length(), URLSpan.class);
        for (URLSpan urlSpan : spans) {
            LinkSpan linkSpan = new LinkSpan(context, urlSpan.getURL(), searchText);
            int spanStart = result.getSpanStart(urlSpan);
            int spanEnd = result.getSpanEnd(urlSpan);
            result.setSpan(linkSpan, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            result.removeSpan(urlSpan);
        }
        return result;
    }

    public void updateUrlList(String url) {
        urls.append(url).append("\n");
    }
}
