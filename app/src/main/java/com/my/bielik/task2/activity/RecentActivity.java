package com.my.bielik.task2.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.my.bielik.task2.PhotoAdapter;
import com.my.bielik.task2.R;
import com.my.bielik.task2.database.PhotosDBHelper;

import static com.my.bielik.task2.activity.LoginActivity.*;

public class RecentActivity extends AppCompatActivity {

    private RecyclerView rvRecentPhotos;

    private PhotoAdapter adapter;
    private PhotosDBHelper dbHelper;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);

        rvRecentPhotos = findViewById(R.id.rv_recent_photos);

        if (getIntent() != null) {
            userId = getIntent().getIntExtra(USER_ID_EXTRA, 0);
        }

        dbHelper = new PhotosDBHelper(this);

        setUpRecyclerView();
        updateDataSet();
    }

    public void setUpRecyclerView() {
        rvRecentPhotos.setLayoutManager(new LinearLayoutManager(this));

        PhotoAdapter.OnItemClickListener listener = new PhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(RecentActivity.this, PhotoActivity.class);
                intent.putExtra(SEARCH_TEXT_EXTRA, adapter.getDataSet().get(position).getSearchText());
                intent.putExtra(URL_EXTRA, adapter.getDataSet().get(position).getUrl());
                intent.putExtra(USER_ID_EXTRA, userId);
                startActivity(intent);
            }
        };

        adapter = new PhotoAdapter(listener);
        rvRecentPhotos.setAdapter(adapter);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateDataSet();
    }

    public void updateDataSet() {
        adapter.updateDataSetWithDB(dbHelper, userId);
        adapter.notifyDataSetChanged();
    }
}
