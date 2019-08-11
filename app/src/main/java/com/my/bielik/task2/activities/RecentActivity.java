package com.my.bielik.task2.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.my.bielik.task2.R;
import com.my.bielik.task2.database_objects.DatabasePhotoItem;
import com.my.bielik.task2.databases.PhotosDBHelper;

public class RecentActivity extends AppCompatActivity {

    private TextView tvRecent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);

        tvRecent = findViewById(R.id.tv_recent);

        PhotosDBHelper recentDBHelper = new PhotosDBHelper(this);

        DatabasePhotoItem databasePhotoItem = recentDBHelper.getRecentPhotos(this);

        tvRecent.setMovementMethod(LinkMovementMethod.getInstance());
        tvRecent.setText(databasePhotoItem.getSpannableUrl(), TextView.BufferType.SPANNABLE);
    }
}
