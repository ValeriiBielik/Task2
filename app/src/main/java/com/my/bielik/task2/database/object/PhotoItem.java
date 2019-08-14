package com.my.bielik.task2.database.object;

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.text.util.Linkify;

import com.my.bielik.task2.LinkSpan;

public class PhotoItem {

    private String searchText;
    private StringBuilder urls = new StringBuilder();
    private int userId;
    private int photoId;

    public PhotoItem(String searchText, int userId) {
        this.searchText = searchText;
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public int getPhotoId() {
        return photoId;
    }

    public String getSearchText() {
        return searchText;
    }

    public String getUrl() {
        return urls.toString();
    }

    public Spannable getSpannableUrl(Context context) {
        final Spannable result = new SpannableString(Html.fromHtml(urls.toString()));

        Linkify.addLinks(result, Linkify.WEB_URLS);

        URLSpan[] spans = result.getSpans(0, result.length(), URLSpan.class);
        for (URLSpan urlSpan : spans) {
            LinkSpan linkSpan = new LinkSpan(context, urlSpan.getURL(), searchText, userId);
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
