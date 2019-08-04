package com.my.bielik.task2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.my.bielik.task2.PhotoItem;
import com.my.bielik.task2.R;
import com.my.bielik.task2.URLManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView tvResult;
    private EditText etRequest;

    private RequestQueue rq;
    private StringBuilder result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tv_result);
        etRequest = findViewById(R.id.et_request);

        rq = Volley.newRequestQueue(this);
        result = new StringBuilder();

    }

    public void search(View view) {
        startLoading(etRequest.getText().toString());
    }

    private void startLoading(final String text) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "In thread");
                stopLoading();

                URLManager.getInstance().setSearchText(text);
                JsonObjectRequest request = new JsonObjectRequest(URLManager.getInstance().getItemUrl(), null, listener, errorListener);
                rq.add(request);
            }
        }).start();

    }

    private void stopLoading() {
        if (rq != null) {
            rq.cancelAll(TAG);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stopLoading();
    }

    private Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                result.setLength(0);
                JSONObject photos = response.getJSONObject("photos");

                JSONArray photoArr = photos.getJSONArray("photo");
                for (int i = 0; i < photoArr.length(); i++) {
                    JSONObject itemObj = photoArr.getJSONObject(i);
                    PhotoItem item = new PhotoItem(
                            itemObj.getString("id"),
                            itemObj.getString("secret"),
                            itemObj.getString("server"),
                            itemObj.getString("farm")
                    );
                    result.append(item.getUrl()).append("\n");
                }
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }

            final Spannable spannable = new SpannableString(Html.fromHtml(result.toString()));
            Linkify.addLinks(spannable, Linkify.WEB_URLS);

            URLSpan[] spans = spannable.getSpans(0, spannable.length(), URLSpan.class);
            for (URLSpan urlSpan : spans) {
                LinkSpan linkSpan = new LinkSpan(urlSpan.getURL());
                int spanStart = spannable.getSpanStart(urlSpan);
                int spanEnd = spannable.getSpanEnd(urlSpan);
                spannable.setSpan(linkSpan, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.removeSpan(urlSpan);
            }
            Handler threadHandler = new Handler(Looper.getMainLooper());
            threadHandler.post(new Runnable() {
                @Override
                public void run() {
                    tvResult.setMovementMethod(LinkMovementMethod.getInstance());
                    tvResult.setText(spannable, TextView.BufferType.SPANNABLE);
                }
            });

        }

    };

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG, error.getMessage());
        }
    };

    private class LinkSpan extends URLSpan {
        private LinkSpan(String url) {
            super(url);
        }

        @Override
        public void onClick(View view) {
            String url = getURL();
            if (url != null) {
                startActivity(new Intent(MainActivity.this, PhotoActivity.class).putExtra("url", url));
            }
        }
    }
}
