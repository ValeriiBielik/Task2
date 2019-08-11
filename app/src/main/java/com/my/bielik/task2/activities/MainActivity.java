package com.my.bielik.task2.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.my.bielik.task2.database_objects.DatabasePhotoItem;
import com.my.bielik.task2.threds.ProcessResponseThread;
import com.my.bielik.task2.R;
import com.my.bielik.task2.response_objects.FlickrResponse;
import com.my.bielik.task2.response_objects.PhotoItem;
import com.my.bielik.task2.retro.FlickrApi;
import com.my.bielik.task2.retro.Retro;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;

import static com.my.bielik.task2.activities.LoginActivity.USER_ID_EXTRA;

public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "539bab6327cc06a832b5793853bac293";
    private static final String TAG = "MainActivity";

    public static final String APP_PREFERENCES = "app_prefs";
    public static final String LAST_SEARCH_VALUE = "last_search_value";

    private ProcessResponseThread processResponseThread = new ProcessResponseThread();

    private TextView tvResult;
    private EditText etRequest;

    private FlickrApi flickrApi;
    private SharedPreferences preferences;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tv_result);
        etRequest = findViewById(R.id.et_request);

        flickrApi = Retro.buildFlickrApi();

        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        if (preferences.contains(LAST_SEARCH_VALUE)) {
            etRequest.setText(preferences.getString(LAST_SEARCH_VALUE, ""));
        }

        if (getIntent() != null) {
            userId = getIntent().getIntExtra(USER_ID_EXTRA, 0);
        }
        processResponseThread.start();

    }

    public void search(View view) {
        getPhotos(etRequest.getText().toString());
    }

    public void openRecentPhotoList(View view) {
        startActivity(new Intent(this, RecentActivity.class).putExtra(USER_ID_EXTRA, userId));
    }

    public void openFavouritePhotoList(View view) {
        startActivity(new Intent(this, FavouritesActivity.class).putExtra(USER_ID_EXTRA, userId));
    }

    void getPhotos(String text) {
        processResponseThread.getHandler().post(new PhotoSearchRunnable(this, text));
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LAST_SEARCH_VALUE, etRequest.getText().toString());
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        processResponseThread.quit();
    }

    private class PhotoSearchRunnable implements Runnable {

        private String text;
        private Context context;

        PhotoSearchRunnable(Context context, String text) {
            this.context = context;
            this.text = text;
        }

        @Override
        public void run() {
            Call<FlickrResponse> call = flickrApi.getPhotos(API_KEY, text, "photos");

            Handler handler = new Handler(Looper.getMainLooper());

            try {
                FlickrResponse flickrResponse = call.execute().body();
                if (flickrResponse != null) {
                    if (flickrResponse.getStat().equals(FlickrResponse.STAT_OK)) {
                        ArrayList<PhotoItem> photos = flickrResponse.getPhotos().getPhoto();
                        DatabasePhotoItem databasePhotoItem = new DatabasePhotoItem(context, text, userId);

                        for (int i = 0; i < photos.size(); i++) {
                            databasePhotoItem.updateUrlList(photos.get(i).getUrl());
                        }

                        final Spannable result = databasePhotoItem.getSpannableUrl();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                tvResult.setMovementMethod(LinkMovementMethod.getInstance());
                                tvResult.setText(result, TextView.BufferType.SPANNABLE);
                            }
                        });
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }
}
