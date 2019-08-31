package com.my.bielik.task2.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.my.bielik.task2.ImagesManager;
import com.my.bielik.task2.PhotoLoader;
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

import static com.my.bielik.task2.activity.LoginActivity.PHOTO_ID_EXTRA;
import static com.my.bielik.task2.activity.LoginActivity.SEARCH_TEXT_EXTRA;
import static com.my.bielik.task2.activity.LoginActivity.URL_EXTRA;
import static com.my.bielik.task2.activity.LoginActivity.USER_ID_EXTRA;
import static com.my.bielik.task2.activity.MainActivity.API_KEY;

public class PhotoActivity extends AppCompatActivity {

    private TextView tvSearchInfo;
    private ImageView imageView;

    private PhotoItem photoItem;
    private DBPhotoHelper photosDBHelper;

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        tvSearchInfo = findViewById(R.id.tv_search_info);
        imageView = findViewById(R.id.image_view);

        if (getIntent() != null) {
            if (getIntent().getType() != null && getIntent().getType().equals("path")) {
                PhotoLoader.createBitmap(getIntent().getStringExtra("path_extra"));
            } else {
                photoItem = new PhotoItem(getIntent().getStringExtra(SEARCH_TEXT_EXTRA),
                        getIntent().getStringExtra(URL_EXTRA),
                        getIntent().getIntExtra(USER_ID_EXTRA, 0),
                        getIntent().getStringExtra(PHOTO_ID_EXTRA));
                PhotoLoader.setPhotoItem(photoItem);
            }
        }

        photosDBHelper = new DBPhotoHelper(this);

        PhotoLoader.setView(imageView);

        if (PhotoLoader.getType() == PhotoLoader.PHOTO_ITEM_LOAD) {
            tvSearchInfo.setText(photoItem.getSearchText());
        }
        addToRecent();
    }

    public void addToFavourites(View view) {
        if (PhotoLoader.getType() == PhotoLoader.PHOTO_ITEM_LOAD) {
            String response = photosDBHelper.addFavourite(photoItem)
                    ? getString(R.string.toast_added_to_favourites) : getString(R.string.toast_in_favourites);
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
        }
    }

    public void removeFromFavourites(View view) {
        if (PhotoLoader.getType() == PhotoLoader.PHOTO_ITEM_LOAD) {
            String response = photosDBHelper.removeFavourite(photoItem)
                    ? getString(R.string.toast_deleted_from_favourites) : getString(R.string.toast_not_in_favourites);
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
        }
    }

    public void addToRecent() {
        if (PhotoLoader.getType() == PhotoLoader.PHOTO_ITEM_LOAD) {
            photosDBHelper.addRecent(photoItem);
        }
    }

    public void downloadImage(View view) {
        if (PhotoLoader.getType() == PhotoLoader.PHOTO_ITEM_LOAD) {
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
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new DownloadImageAsyncTask(photoItem).execute();
                } else {
                    Toast.makeText(this, getString(R.string.toast_required_write_external_storage_permission), Toast.LENGTH_SHORT).show();
                }
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
                System.out.println(e);
            }

            return null;
        }
    }
}