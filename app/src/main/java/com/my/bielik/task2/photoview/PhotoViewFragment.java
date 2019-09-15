package com.my.bielik.task2.photoview;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.my.bielik.task2.R;
import com.my.bielik.task2.api.Retro;
import com.my.bielik.task2.api.response.object.ImageResponse;
import com.my.bielik.task2.api.response.object.ImageSizesResponse;
import com.my.bielik.task2.api.response.object.PhotoListResponse;
import com.my.bielik.task2.database.DBPhotoHelper;
import com.my.bielik.task2.database.object.PhotoItem;
import com.my.bielik.task2.gallery.ImagesManager;

import java.io.IOException;
import java.net.URL;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import retrofit2.Call;

import static com.my.bielik.task2.app.MyApplication.API_KEY;
import static com.my.bielik.task2.user.LoginActivity.PHOTO_ID_EXTRA;
import static com.my.bielik.task2.user.LoginActivity.SEARCH_TEXT_EXTRA;
import static com.my.bielik.task2.user.LoginActivity.TAG;
import static com.my.bielik.task2.user.LoginActivity.URL_EXTRA;
import static com.my.bielik.task2.user.LoginActivity.USER_ID_EXTRA;

public class PhotoViewFragment extends Fragment {

    private TextView tvSearchInfo;
    private ImageView imageView;
    private BottomNavigationView bottomNavigationView;

    private PhotoItem photoItem;
    private Bitmap bitmap;
    private int imageLoadType;

    private DBPhotoHelper photosDBHelper;

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private static final int PHOTO_ITEM_LOAD = 1;
    private static final int BITMAP_LOAD = 2;

    public PhotoViewFragment() {
    }

    public static PhotoViewFragment newInstance(String title, String url, int userId, String photoId) {
        PhotoViewFragment fragment = new PhotoViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SEARCH_TEXT_EXTRA, title);
        bundle.putString(URL_EXTRA, url);
        bundle.putInt(USER_ID_EXTRA, userId);
        bundle.putString(PHOTO_ID_EXTRA, photoId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_view, container, false);

        tvSearchInfo = view.findViewById(R.id.tv_search_info);
        imageView = view.findViewById(R.id.image_view);
        bottomNavigationView = view.findViewById(R.id.bottom_navigation);

        if (getArguments() != null) {
            if (getArguments().getString("path_extra") != null) {
                bitmap = BitmapFactory.decodeFile(getArguments().getString("path_extra"));
                imageLoadType = BITMAP_LOAD;
            } else {
                photoItem = new PhotoItem(getArguments().getString(SEARCH_TEXT_EXTRA),
                        getArguments().getString(URL_EXTRA),
                        getArguments().getInt(USER_ID_EXTRA),
                        getArguments().getString(PHOTO_ID_EXTRA));
                imageLoadType = PHOTO_ITEM_LOAD;
            }
        }

        photosDBHelper = new DBPhotoHelper(getActivity());

        setContent();
        setBottomNavigationView();
        addToRecent();

        return view;
    }

    private void setContent() {
        switch (imageLoadType) {
            case BITMAP_LOAD: {
                imageView.setImageBitmap(bitmap);
                bottomNavigationView.setVisibility(View.INVISIBLE);
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

    private void addToFavourites() {
        if (imageLoadType == PHOTO_ITEM_LOAD) {
            String response = photosDBHelper.addFavourite(photoItem)
                    ? getString(R.string.toast_added_to_favourites) : getString(R.string.toast_in_favourites);
            Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
        }
    }

    private void removeFromFavourites() {
        if (imageLoadType == PHOTO_ITEM_LOAD) {
            String response = photosDBHelper.removeFavourite(photoItem)
                    ? getString(R.string.toast_deleted_from_favourites) : getString(R.string.toast_not_in_favourites);
            Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
        }
    }

    private void addToRecent() {
        if (imageLoadType == PHOTO_ITEM_LOAD) {
            photosDBHelper.addRecent(photoItem);
        }
    }

    private void downloadImage() {
        if (imageLoadType == PHOTO_ITEM_LOAD) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                new DownloadImageAsyncTask(photoItem).execute();
                Toast.makeText(getActivity(), getString(R.string.toast_download_completed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new DownloadImageAsyncTask(photoItem).execute();
            } else {
                Toast.makeText(getActivity(), getString(R.string.toast_required_write_external_storage_permission), Toast.LENGTH_SHORT).show();
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
