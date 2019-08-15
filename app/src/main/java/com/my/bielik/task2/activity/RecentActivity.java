package com.my.bielik.task2.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.my.bielik.task2.PhotoAdapter;
import com.my.bielik.task2.R;
import com.my.bielik.task2.database.object.PhotoItem;
import com.my.bielik.task2.database.PhotosDBHelper;

import java.util.ArrayList;
import java.util.List;

import static com.my.bielik.task2.activity.LoginActivity.*;

public class RecentActivity extends AppCompatActivity {

    private RecyclerView rvRecentPhotos;

    private PhotoAdapter adapter;
    private PhotosDBHelper dbHelper;

    private int userId;
    private List<PhotoItem> dataSet = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);

        rvRecentPhotos = findViewById(R.id.rv_recent_photos);

        if (getIntent() != null) {
            userId = getIntent().getIntExtra(USER_ID_EXTRA, 0);
        }

        dbHelper = new PhotosDBHelper(this);

        rvRecentPhotos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PhotoAdapter(dataSet);
        rvRecentPhotos.setAdapter(adapter);

        adapter.setOnItemClickListener(new PhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder) {
                Intent intent = new Intent(RecentActivity.this, PhotoActivity.class);
                intent.putExtra(SEARCH_TEXT_EXTRA, dataSet.get(viewHolder.getAdapterPosition()).getSearchText());
                intent.putExtra(URL_EXTRA, dataSet.get(viewHolder.getAdapterPosition()).getUrl());
                intent.putExtra(USER_ID_EXTRA, userId);
                startActivity(intent);
            }
        });

        updateDataSet();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateDataSet();
    }

    public void updateDataSet() {
        dataSet = dbHelper.getRecentPhotos(dataSet, userId);
        adapter.notifyDataSetChanged();
    }
}
