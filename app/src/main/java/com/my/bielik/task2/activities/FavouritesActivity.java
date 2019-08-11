package com.my.bielik.task2.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.my.bielik.task2.FavouritesAdapter;
import com.my.bielik.task2.R;
import com.my.bielik.task2.database_objects.DatabasePhotoItem;
import com.my.bielik.task2.databases.PhotosDBHelper;

import java.util.ArrayList;

import static com.my.bielik.task2.activities.LoginActivity.USER_ID_EXTRA;

public class FavouritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private PhotosDBHelper photosDBHelper;
    private FavouritesAdapter adapter;

    private int userId;
    private ArrayList<DatabasePhotoItem> databasePhotoItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        recyclerView = findViewById(R.id.recycler_view_favourites);

        if (getIntent() != null) {
            userId = getIntent().getIntExtra(USER_ID_EXTRA, 0);
        }
        photosDBHelper = new PhotosDBHelper(this);
        updateFavouriteItemsList();
    }

    public void updateFavouriteItemsList() {
        databasePhotoItems = photosDBHelper.getFavouritePhotos(this, databasePhotoItems, userId);
        if (databasePhotoItems.size() == 0) {
            Toast.makeText(this, getString(R.string.toast_no_favourites), Toast.LENGTH_SHORT).show();
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new FavouritesAdapter(this, databasePhotoItems);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateFavouriteItemsList();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }
}
