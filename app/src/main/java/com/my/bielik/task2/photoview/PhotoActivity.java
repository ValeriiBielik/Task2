package com.my.bielik.task2.photoview;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.my.bielik.task2.gallery.ImagesManager;
import com.my.bielik.task2.R;
import com.my.bielik.task2.api.Retro;
import com.my.bielik.task2.api.response.object.ImageResponse;
import com.my.bielik.task2.api.response.object.ImageSizesResponse;
import com.my.bielik.task2.api.response.object.PhotoListResponse;
import com.my.bielik.task2.database.DBPhotoHelper;
import com.my.bielik.task2.database.object.PhotoItem;

import java.io.IOException;
import java.net.URL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import retrofit2.Call;

import static com.my.bielik.task2.user.LoginActivity.PHOTO_ID_EXTRA;
import static com.my.bielik.task2.user.LoginActivity.SEARCH_TEXT_EXTRA;
import static com.my.bielik.task2.user.LoginActivity.URL_EXTRA;
import static com.my.bielik.task2.user.LoginActivity.USER_ID_EXTRA;
import static com.my.bielik.task2.main.MainActivity.API_KEY;

public class PhotoActivity extends AppCompatActivity {

    private static final String TAG = "PhotoActivity";

    private TextView tvSearchInfo;
    private ImageView imageView;
    private BottomNavigationView bottomNavigationView;

    private PhotoItem photoItem;
    private Bitmap bitmap;
    private int imageLoadType;

    private DBPhotoHelper photosDBHelper;

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    public static final int PHOTO_ITEM_LOAD = 1;
    public static final int BITMAP_LOAD = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        tvSearchInfo = findViewById(R.id.tv_search_info);
        imageView = findViewById(R.id.image_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (getIntent() != null) {
            if (getIntent().getType() != null && getIntent().getType().equals("path")) {
                bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("path_extra"));
                imageLoadType = BITMAP_LOAD;
            } else {
                photoItem = new PhotoItem(getIntent().getStringExtra(SEARCH_TEXT_EXTRA),
                        getIntent().getStringExtra(URL_EXTRA),
                        getIntent().getIntExtra(USER_ID_EXTRA, 0),
                        getIntent().getStringExtra(PHOTO_ID_EXTRA));
                imageLoadType = PHOTO_ITEM_LOAD;
            }
        }
        photosDBHelper = new DBPhotoHelper(this);

        setContent();
        setBottomNavigationView();
        addToRecent();
    }

    private void setContent() {
        switch (imageLoadType) {
            case BITMAP_LOAD: {
                imageView.setImageBitmap(bitmap);
                break;
            }
            case PHOTO_ITEM_LOAD: {
                Glide.with(this).load(photoItem.getUrl()).into(imageView);
                tvSearchInfo.setText(photoItem.getSearchText());
                break;
            }
        }
    }

    private void setBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_add_to_favourites : {
                        addToFavourites();
                        return true;
                    }
                    case R.id.menu_item_delete_from_favourites : {
                        removeFromFavourites();
                        return true;
                    }
                    case R.id.menu_item_download : {
                        downloadImage();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void addToFavourites() {
        if (imageLoadType == PHOTO_ITEM_LOAD) {
            String response = photosDBHelper.addFavourite(photoItem)
                    ? getString(R.string.toast_added_to_favourites) : getString(R.string.toast_in_favourites);
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
        }
    }

    public void removeFromFavourites() {
        if (imageLoadType == PHOTO_ITEM_LOAD) {
            String response = photosDBHelper.removeFavourite(photoItem)
                    ? getString(R.string.toast_deleted_from_favourites) : getString(R.string.toast_not_in_favourites);
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
        }
    }

    public void addToRecent() {
        if (imageLoadType == PHOTO_ITEM_LOAD) {
            photosDBHelper.addRecent(photoItem);
        }
    }

    public void downloadImage() {
        if (imageLoadType == PHOTO_ITEM_LOAD) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                new DownloadImageAsyncTask(photoItem).execute();
                Toast.makeText(this, getString(R.string.toast_download_completed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new DownloadImageAsyncTask(photoItem).execute();
            } else {
                Toast.makeText(this, getString(R.string.toast_required_write_external_storage_permission), Toast.LENGTH_SHORT).show();
            }
        }

    }

    static class DownloadImageAsyncTask extends AsyncTask<Void, Void, Void> {

        private PhotoItem photoItem;

        DownloadImageAsyncTask(PhotoItem photoItem) {
            this.photoItem = photoItem;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Call<ImageSizesResponse> call = Retro.getFlickrApi().getPhotoToDownload(API_KEY, photoItem.getPhotoId());
                ImageSizesResponse imageSizesResponse = call.execute().body();

                if (imageSizesResponse != null || imageSizesResponse.getStat().equals(PhotoListResponse.STAT_OK)) {

                    ImageResponse imageResponse = null;
                    for (ImageResponse i : imageSizesResponse.getSizes().getSize()) {
                        if (i.getLabel().equals(ImageResponse.SizesContract.SIZE_LARGE)) {
                            imageResponse = i;
                            break;
                        }
                    }
                    if (imageResponse == null) {
                        imageResponse = imageSizesResponse.getSizes().getSize().get(imageSizesResponse.getSizes().getSize().size() - 1);
                    }

                    URL url = new URL(imageResponse.getSource());
                    Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    ImagesManager imagesManager = ImagesManager.getInstance();
                    imagesManager.createPublicStorage();
                    imagesManager.createImageFile(photoItem.getSearchText(), image, ImagesManager.CREATE_PUBLIC_FILE);

                }
            } catch (IOException e) {
                Log.e(TAG, "", e);
            }

            return null;
        }
    }
}