package com.my.bielik.task2.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.widget.Toast;

import com.my.bielik.task2.R;
import com.my.bielik.task2.database.PhotosDBHelper;
import com.my.bielik.task2.database.object.PhotoItem;
import com.my.bielik.task2.favourites.FavouritesAdapter;
import com.my.bielik.task2.favourites.Header;
import com.my.bielik.task2.favourites.Photo;

import static com.my.bielik.task2.activity.LoginActivity.SEARCH_TEXT_EXTRA;
import static com.my.bielik.task2.activity.LoginActivity.URL_EXTRA;
import static com.my.bielik.task2.activity.LoginActivity.USER_ID_EXTRA;

public class FavouritesActivity extends AppCompatActivity {

    private RecyclerView rvFavourites;

    private PhotosDBHelper dbHelper;
    private FavouritesAdapter adapter;

    private int userId;

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

        FavouritesAdapter.OnItemClickListener onItemClickListener = new FavouritesAdapter.OnItemClickListener() {
            @Override
            public void onClickListener(int position) {
                Intent intent = new Intent(FavouritesActivity.this, PhotoActivity.class);
                intent.putExtra(SEARCH_TEXT_EXTRA, ((Photo) adapter.getDataSet().get(position)).getSearchText());
                intent.putExtra(URL_EXTRA, ((Photo) adapter.getDataSet().get(position)).getUrl());
                intent.putExtra(USER_ID_EXTRA, userId);
                startActivity(intent);
            }
        };

        FavouritesAdapter.OnRemoveButtonClickListener onRemoveButtonClickListener = new FavouritesAdapter.OnRemoveButtonClickListener() {
            @Override
            public void onClickListener(int position) {
                removeItem(position);
            }
        };

        adapter = new FavouritesAdapter(onItemClickListener, onRemoveButtonClickListener);
        rvFavourites.setAdapter(adapter);

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
        if (adapter.getDataSet().get(position) instanceof Header) {
            adapter.notifyDataSetChanged();
            return;
        }
        PhotoItem photoItem = new PhotoItem(((Photo) adapter.getDataSet().get(position)).getSearchText(),
                ((Photo) adapter.getDataSet().get(position)).getUrl(), userId);
        dbHelper.removeFavourite(photoItem);
        adapter.removeDateItem(position);
        adapter.notifyItemRemoved(position);

        if (dbHelper.getFavouritePhotoCount(photoItem) == 0) {
            if (--position != RecyclerView.NO_POSITION) {
                adapter.removeDateItem(position);
                adapter.notifyItemRemoved(position);
            }
        }
    }

    public void updateFavouriteItemsList() {
        adapter.updateDataSet(dbHelper, userId);
        if (adapter.getDataSet().size() == 0) {
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
