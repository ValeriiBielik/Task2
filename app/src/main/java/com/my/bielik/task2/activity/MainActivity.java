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
import android.widget.Toast;

import com.my.bielik.task2.PhotoAdapter;
import com.my.bielik.task2.R;
import com.my.bielik.task2.api.FlickrApi;
import com.my.bielik.task2.api.Retro;
import com.my.bielik.task2.api.response.FlickrResponse;
import com.my.bielik.task2.api.response.object.ResponsePhotoItem;
import com.my.bielik.task2.database.object.PhotoItem;
import com.my.bielik.task2.threds.ProcessResponseThread;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import retrofit2.Call;

import static com.my.bielik.task2.activity.LoginActivity.SEARCH_TEXT_EXTRA;
import static com.my.bielik.task2.activity.LoginActivity.TAG;
import static com.my.bielik.task2.activity.LoginActivity.URL_EXTRA;
import static com.my.bielik.task2.activity.LoginActivity.USER_ID_EXTRA;


public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "539bab6327cc06a832b5793853bac293";

    public static final String APP_PREFERENCES = "app_prefs";
    public static final String LAST_SEARCH_VALUE = "last_search_value";

    private ProcessResponseThread processResponseThread = new ProcessResponseThread();

    private RecyclerView rvPhotos;
    private EditText etRequest;

    private FlickrApi flickrApi;
    private SharedPreferences preferences;
    private PhotoSearchRunnable runnable;

    private PhotoAdapter adapter;
    private LinearLayoutManager layoutManager;

    private int userId;
    private boolean isLoading;

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

        setRecyclerView();
        runnable = new PhotoSearchRunnable(this, userId);
        processResponseThread.start();
    }

    public void search(View view) {
        getPhotos(etRequest.getText().toString().trim());
    }

    public void openRecentPhotoList(View view) {
        startActivity(new Intent(this, RecentActivity.class).putExtra(USER_ID_EXTRA, userId));
    }

    public void openFavouritePhotoList(View view) {
        startActivity(new Intent(this, FavouritesActivity.class).putExtra(USER_ID_EXTRA, userId));
    }

    public void setRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        rvPhotos.setLayoutManager(layoutManager);

        PhotoAdapter.OnItemClickListener listener = new PhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MainActivity.this, PhotoActivity.class);
                intent.putExtra(URL_EXTRA, adapter.getDataSet().get(position).getUrl());
                intent.putExtra(SEARCH_TEXT_EXTRA, adapter.getDataSet().get(position).getSearchText());
                intent.putExtra(USER_ID_EXTRA, userId);
                startActivity(intent);
            }
        };
        adapter = new PhotoAdapter(listener);
        rvPhotos.setAdapter(adapter);

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

        rvPhotos.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading) {
                    if (firstVisibleItem >= totalItemCount * 0.8) {
                        isLoading = true;
                        loadMorePhotos();
                    }
                }
            }
        });
    }

    void getPhotos(String text) {
        runnable.setText(text);
        runnable.resetPage();
        processResponseThread.getHandler().post(runnable);
    }

    public void loadMorePhotos() {
        if (!runnable.updatePage()) {
            Toast.makeText(this, getString(R.string.toast_no_more_photos), Toast.LENGTH_SHORT).show();
            return;
        }
        processResponseThread.getHandler().post(runnable);
    }

    public void removeItem(int position) {
        adapter.removeDataItem(position);
        adapter.notifyItemRemoved(position);
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
        private int page = 1;
        private int pagesCount;
        private boolean isUpdating;

        private WeakReference<MainActivity> activityWeakReference;

        PhotoSearchRunnable(MainActivity activity, int userId) {
            activityWeakReference = new WeakReference<>(activity);
            this.userId = userId;
        }

        void setText(String text) {
            this.text = text;
        }

        boolean updatePage() {
            if (page == pagesCount)
                return false;

            isUpdating = true;
            page++;
            return true;
        }

        void resetPage() {
            isUpdating = false;
            page = 1;
        }

        @Override
        public void run() {
            Log.e(TAG, "Runnable.run : page " + page);
            final MainActivity activity = activityWeakReference.get();

            if (activity == null || activity.isFinishing()) {
                return;
            }

            Call<FlickrResponse> call = activity.flickrApi.getPhotos(API_KEY, text, "photos", page);
            Handler handler = new Handler(Looper.getMainLooper());

            try {
                FlickrResponse flickrResponse = call.execute().body();
                if (flickrResponse != null) {
                    if (flickrResponse.getStat().equals(FlickrResponse.STAT_OK)) {
                        List<ResponsePhotoItem> photos = flickrResponse.getPhotos().getPhoto();

                        if (!isUpdating) {
                            activity.adapter.clearDataSet();
                        }

                        for (int i = 0; i < photos.size(); i++) {
                            PhotoItem photoItem = new PhotoItem(text, photos.get(i).getUrl(), userId);
                            activity.adapter.updateDataSet(photoItem);
                        }

                        if (page == 1) {
                            pagesCount = flickrResponse.getPhotos().getPages();
                        }

                        Log.e(TAG, "last page: " + flickrResponse.getPhotos().getPages());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                activity.adapter.notifyDataSetChanged();
                                activity.isLoading = false;
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
