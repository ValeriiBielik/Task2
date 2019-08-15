package com.my.bielik.task2.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;

import com.my.bielik.task2.database.object.PhotoItem;
import com.my.bielik.task2.favourites.Header;
import com.my.bielik.task2.favourites.Photo;
import com.my.bielik.task2.favourites.RowType;
import com.my.bielik.task2.favourites.FavouritesAdapter;
import com.my.bielik.task2.R;
import com.my.bielik.task2.database.PhotosDBHelper;

import java.util.ArrayList;
import java.util.List;

import static com.my.bielik.task2.activity.LoginActivity.*;

public class FavouritesActivity extends AppCompatActivity {

    private RecyclerView rvFavourites;

    private PhotosDBHelper dbHelper;
    private FavouritesAdapter adapter;

    private int userId;
    private List<RowType> dataSet = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        rvFavourites = findViewById(R.id.recycler_view_favourites);

        if (getIntent() != null) {
            userId = getIntent().getIntExtra(USER_ID_EXTRA, 0);
        }

        dbHelper = new PhotosDBHelper(this);

        setUpRecyclerView();
        updateFavouriteItemsList();
    }

    public void setUpRecyclerView() {
        rvFavourites.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FavouritesAdapter(dataSet);
        rvFavourites.setAdapter(adapter);

        adapter.setOnItemClickListener(new FavouritesAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(RecyclerView.ViewHolder viewHolder) {
                Intent intent = new Intent(FavouritesActivity.this, PhotoActivity.class);
                intent.putExtra(SEARCH_TEXT_EXTRA, ((Photo) dataSet.get(viewHolder.getAdapterPosition())).getSearchText());
                intent.putExtra(URL_EXTRA, ((Photo) dataSet.get(viewHolder.getAdapterPosition())).getUrl());
                intent.putExtra(USER_ID_EXTRA, userId);
                startActivity(intent);
            }
        });

        adapter.setOnRemoveButtonClickListener(new Photo.OnRemoveButtonClickListener() {
            @Override
            public void onClickListener(RecyclerView.ViewHolder viewHolder) {
                removeItem(viewHolder.getAdapterPosition());
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
        }).attachToRecyclerView(rvFavourites);
    }

    public void removeItem(int position) {
        if (dataSet.get(position) instanceof Header) {
            adapter.notifyDataSetChanged();
            return;
        }
        PhotoItem photoItem = new PhotoItem(((Photo) dataSet.get(position)).getSearchText(), ((Photo) dataSet.get(position)).getUrl(), userId);
        dbHelper.removeFavourite(photoItem);
        dataSet.remove(position);
        adapter.notifyItemRemoved(position);

        if (dbHelper.getFavouritePhotoCount(photoItem) == 0) {
            if (--position != RecyclerView.NO_POSITION) {
                dataSet.remove(position);
                adapter.notifyItemRemoved(position);
            }
        }
    }

    public void updateFavouriteItemsList() {
        dataSet = dbHelper.getFavouritePhotos(dataSet, userId);
        if (dataSet.size() == 0) {
            Toast.makeText(this, getString(R.string.toast_no_favourites), Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateFavouriteItemsList();
    }
}
