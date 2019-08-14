package com.my.bielik.task2.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.my.bielik.task2.PhotoAdapter;
import com.my.bielik.task2.database.object.PhotoItem;
import com.my.bielik.task2.threds.ProcessResponseThread;
import com.my.bielik.task2.R;
import com.my.bielik.task2.retro.response.FlickrResponse;
import com.my.bielik.task2.retro.response.object.ResponsePhotoItem;
import com.my.bielik.task2.retro.FlickrApi;
import com.my.bielik.task2.retro.Retro;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

import static com.my.bielik.task2.activity.LoginActivity.*;

public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "539bab6327cc06a832b5793853bac293";

    public static final String APP_PREFERENCES = "app_prefs";
    public static final String LAST_SEARCH_VALUE = "last_search_value";

    private ProcessResponseThread processResponseThread = new ProcessResponseThread();

    private RecyclerView rvPhotos;
    private EditText etRequest;

    private FlickrApi flickrApi;
    private SharedPreferences preferences;

    private PhotoAdapter adapter;
    private volatile List<PhotoItem> photoItems = new ArrayList<>();
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvPhotos = findViewById(R.id.rv_photos);
        etRequest = findViewById(R.id.et_request);

        flickrApi = Retro.buildFlickrApi();

        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        if (preferences.contains(LAST_SEARCH_VALUE)) {
            etRequest.setText(preferences.getString(LAST_SEARCH_VALUE, ""));
        }

        if (getIntent() != null) {
            userId = getIntent().getIntExtra(USER_ID_EXTRA, 0);
        }

        rvPhotos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PhotoAdapter(photoItems);
        rvPhotos.setAdapter(adapter);

        adapter.setOnItemClickListener(new PhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder) {
                Intent intent = new Intent(MainActivity.this, PhotoActivity.class);
                intent.putExtra(URL_EXTRA, photoItems.get(viewHolder.getAdapterPosition()).getUrl());
                intent.putExtra(SEARCH_TEXT_EXTRA, photoItems.get(viewHolder.getAdapterPosition()).getSearchText());
                intent.putExtra(USER_ID_EXTRA, userId);
                startActivity(intent);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                removeItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(rvPhotos);
        Log.e(TAG, "MainActivity.onCreate : userId " + userId);

        processResponseThread.start();
    }

    public void removeItem(int position) {
        photoItems.remove(position);
        adapter.notifyItemRemoved(position);
    }

    public void search(View view) {
        getPhotos(etRequest.getText().toString(), true);
    }

    public void openRecentPhotoList(View view) {
        startActivity(new Intent(this, RecentActivity.class).putExtra(USER_ID_EXTRA, userId));
    }

    public void openFavouritePhotoList(View view) {
        startActivity(new Intent(this, FavouritesActivity.class).putExtra(USER_ID_EXTRA, userId));
    }

    void getPhotos(String text, boolean isUpdated) {
        processResponseThread.getHandler().post(new PhotoSearchRunnable(this, text, userId, isUpdated));
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
        private int userId;
        private boolean searchStatus;

        private WeakReference<MainActivity> activityWeakReference;

        PhotoSearchRunnable(MainActivity activity, String text, int userId, boolean isUpdated) {
            activityWeakReference = new WeakReference<>(activity);
            this.text = text;
            this.userId = userId;
            this.searchStatus = isUpdated;
        }

        @Override
        public void run() {
            Log.e(TAG, "Runnable.run : userID " + userId);
            final MainActivity activity = activityWeakReference.get();

            if (activity == null || activity.isFinishing()) {
                return;
            }

            Call<FlickrResponse> call = activity.flickrApi.getPhotos(API_KEY, text, "photos");
            Handler handler = new Handler(Looper.getMainLooper());

            try {
                FlickrResponse flickrResponse = call.execute().body();
                if (flickrResponse != null) {
                    if (flickrResponse.getStat().equals(FlickrResponse.STAT_OK)) {
                        List<ResponsePhotoItem> photos = flickrResponse.getPhotos().getPhoto();

                        if (searchStatus) {
                            activity.photoItems.clear();
                        }

                        for (int i = 0; i < photos.size(); i++) {
                            PhotoItem photoItem = new PhotoItem(text, userId);
                            photoItem.updateUrlList(photos.get(i).getUrl());
                            photoItem.setPhotoId(i);
                            activity.photoItems.add(photoItem);
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                activity.adapter.notifyDataSetChanged();
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
