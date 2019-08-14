package com.my.bielik.task2.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.my.bielik.task2.PhotoAdapter;
import com.my.bielik.task2.R;
import com.my.bielik.task2.database.object.PhotoItem;
import com.my.bielik.task2.database.PhotosDBHelper;
import com.my.bielik.task2.favourites.Photo;

import java.util.ArrayList;
import java.util.List;

import static com.my.bielik.task2.activity.LoginActivity.*;

public class RecentActivity extends AppCompatActivity {

    private RecyclerView rvRecentPhotos;
    private PhotoAdapter adapter;
    private List<PhotoItem> photoItems = new ArrayList<>();

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);

        rvRecentPhotos = findViewById(R.id.rv_recent_photos);

        if (getIntent() != null) {
            userId = getIntent().getIntExtra(USER_ID_EXTRA, 0);
        }

        PhotosDBHelper recentDBHelper = new PhotosDBHelper(this);

        rvRecentPhotos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PhotoAdapter(photoItems);
        rvRecentPhotos.setAdapter(adapter);
        adapter.setOnItemClickListener(new PhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder) {
                Intent intent = new Intent(RecentActivity.this, PhotoActivity.class);
                intent.putExtra(SEARCH_TEXT_EXTRA, photoItems.get(viewHolder.getAdapterPosition()).getSearchText());
                intent.putExtra(URL_EXTRA, photoItems.get(viewHolder.getAdapterPosition()).getUrl());
                intent.putExtra(USER_ID_EXTRA, userId);
                startActivity(intent);
            }
        });
        photoItems = recentDBHelper.getRecentPhotos(photoItems, userId);
        adapter.notifyDataSetChanged();

        Log.e(TAG, "RecentActivity.onCreate : userID " + userId);
    }
}
