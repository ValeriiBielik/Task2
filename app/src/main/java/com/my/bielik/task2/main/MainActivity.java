package com.my.bielik.task2.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.my.bielik.task2.database.object.PhotoItem;
import com.my.bielik.task2.map.MapsActivity;
import com.my.bielik.task2.favourites.FavouritesActivity;
import com.my.bielik.task2.gallery.GalleryActivity;
import com.my.bielik.task2.photoview.PhotoActivity;
import com.my.bielik.task2.recent.RecentActivity;
import com.my.bielik.task2.thread.AddressToTitleConvertRunnable;
import com.my.bielik.task2.thread.PhotoSearchRunnable;
import com.my.bielik.task2.R;
import com.my.bielik.task2.thread.ProcessResponseThread;

import java.util.List;

import static com.my.bielik.task2.thread.PhotoSearchRunnable.*;
import static com.my.bielik.task2.user.LoginActivity.*;

public class MainActivity extends AppCompatActivity {

    public static final String API_KEY = "539bab6327cc06a832b5793853bac293";

    public static final int PICK_COORDINATES_REQUEST = 1;
    public static final String APP_PREFERENCES = "app_prefs";
    public static final String LAST_SEARCH_VALUE = "last_search_value";

    private ProcessResponseThread processResponseThread = new ProcessResponseThread();

    private RecyclerView rvPhotos;
    private EditText etRequest;

    private SharedPreferences preferences;
    private PhotoSearchRunnable runnable;

    private PhotoAdapter adapter;
    private LinearLayoutManager layoutManager;

    private int userId;
    private String geoPhotoTitle;
    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvPhotos = findViewById(R.id.rv_photos);
        etRequest = findViewById(R.id.et_request);

        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        if (preferences.contains(LAST_SEARCH_VALUE)) {
            etRequest.setText(preferences.getString(LAST_SEARCH_VALUE, ""));
        }

        if (getIntent() != null) {
            userId = getIntent().getIntExtra(USER_ID_EXTRA, 0);
        }

        setRecyclerView();
        setPhotoSearchRunnable();

        processResponseThread.start();
    }

    public void search(View view) {
        runnable.setText(etRequest.getText().toString().trim());
        getPhotos(SEARCH_PHOTOS_WITH_TEXT);
    }

    public void openRecentPhotoList(View view) {
        startActivity(new Intent(this, RecentActivity.class).putExtra(USER_ID_EXTRA, userId));
    }

    public void openFavouritePhotoList(View view) {
        startActivity(new Intent(this, FavouritesActivity.class).putExtra(USER_ID_EXTRA, userId));
    }

    public void openMap(View view) {
        startActivityForResult(new Intent(this, MapsActivity.class), PICK_COORDINATES_REQUEST);
    }

    public void openGallery(View view) {
        startActivity(new Intent(this, GalleryActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_COORDINATES_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    double latitude = data.getDoubleExtra(LATITUDE_EXTRA, 0);
                    double longitude = data.getDoubleExtra(LONGITUDE_EXTRA, 0);
                    runnable.setGeoCoordinates(latitude, longitude);

                    processResponseThread.getHandler().post(new AddressToTitleConvertRunnable(this, latitude, longitude,
                            new AddressToTitleConvertRunnable.OnConvertingFinishedCallback() {
                        @Override
                        public void onConvertingFinished(String text) {
                            geoPhotoTitle = text;
                        }
                    }));
                    getPhotos(SEARCH_PHOTOS_WITH_GEO_COORDINATES);
                }
            }
        }
    }

    public void getPhotos(int searchType) {
        runnable.setSearchType(searchType);
        runnable.resetPage();
        processResponseThread.getHandler().post(runnable);
    }

    public PhotoAdapter getAdapter() {
        return adapter;
    }

    private void setRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        rvPhotos.setLayoutManager(layoutManager);

        PhotoAdapter.OnItemClickListener listener = new PhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MainActivity.this, PhotoActivity.class);
                intent.putExtra(URL_EXTRA, adapter.getDataSet().get(position).getUrl());
                intent.putExtra(SEARCH_TEXT_EXTRA, adapter.getDataSet().get(position).getSearchText());
                intent.putExtra(USER_ID_EXTRA, userId);
                intent.putExtra(PHOTO_ID_EXTRA, adapter.getDataSet().get(position).getPhotoId());
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

    private void setPhotoSearchRunnable() {
        runnable = new PhotoSearchRunnable(userId, new PhotosFoundCallback() {
            @Override
            public void onPhotosFound(final List<PhotoItem> photoItems, final boolean isUpdating, final int searchType) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (!isUpdating) {
                            adapter.clearDataSet();
                        }

                        PhotoItem photoItem;
                        for (int i = 0; i < photoItems.size(); i++) {
                            photoItem = photoItems.get(i);

                            if (searchType == SEARCH_PHOTOS_WITH_GEO_COORDINATES) {
                                photoItem.setSearchText(geoPhotoTitle);
                            }
                            adapter.updateDataSet(photoItem);
                        }

                        adapter.notifyDataSetChanged();
                        finishLoading();

                        if (adapter.getDataSet().size() == 0) {
                            Toast.makeText(MainActivity.this, getString(R.string.no_photos), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void finishLoading() {
        isLoading = false;
    }

    private void loadMorePhotos() {
        if (!runnable.updatePage()) {
            Toast.makeText(this, getString(R.string.toast_no_more_photos), Toast.LENGTH_SHORT).show();
            return;
        }
        processResponseThread.getHandler().post(runnable);
    }

    private void removeItem(int position) {
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
}
