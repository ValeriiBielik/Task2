package com.my.bielik.task2.recent;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.my.bielik.task2.main.PhotoAdapter;
import com.my.bielik.task2.R;
import com.my.bielik.task2.photoview.PhotoActivity;
import com.my.bielik.task2.database.DBPhotoHelper;

import static com.my.bielik.task2.user.LoginActivity.*;

public class RecentActivity extends AppCompatActivity {

    private RecyclerView rvRecentPhotos;

    private PhotoAdapter adapter;
    private DBPhotoHelper dbHelper;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);

        rvRecentPhotos = findViewById(R.id.rv_recent_photos);

        if (getIntent() != null) {
            userId = getIntent().getIntExtra(USER_ID_EXTRA, 0);
        }

        dbHelper = new DBPhotoHelper(this);

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
                intent.putExtra(PHOTO_ID_EXTRA, adapter.getDataSet().get(position).getPhotoId());
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
