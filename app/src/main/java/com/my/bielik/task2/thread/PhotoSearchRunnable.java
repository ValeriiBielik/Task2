package com.my.bielik.task2.thread;

import android.util.Log;

import com.my.bielik.task2.api.Retro;
import com.my.bielik.task2.api.response.object.PhotoItemResponse;
import com.my.bielik.task2.api.response.object.PhotoListResponse;
import com.my.bielik.task2.database.entity.Photo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

import static com.my.bielik.task2.app.MyApplication.API_KEY;
import static com.my.bielik.task2.user.LoginActivity.TAG;

public class PhotoSearchRunnable implements Runnable {

    public static final int SEARCH_PHOTOS_WITH_TEXT = 1;
    public static final int SEARCH_PHOTOS_WITH_GEO_COORDINATES = 2;

    private Call<PhotoListResponse> call;
    private PhotosFoundCallback photosFoundCallback;

    private int page = 1;
    private int pagesCount;
    private boolean isUpdating;
    private String text;
    private double latitude;
    private double longitude;
    private int searchType;

    public interface PhotosFoundCallback {
        void onPhotosFound(List<Photo> photos, boolean isUpdating, int searchType);
    }

    public PhotoSearchRunnable(PhotosFoundCallback callback) {
        this.photosFoundCallback = callback;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setGeoCoordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public boolean updatePage() {
        if (page == pagesCount)
            return false;

        isUpdating = true;
        page++;
        return true;
    }

    public void resetPage() {
        isUpdating = false;
        page = 1;
    }

    public void setSearchType(int searchType) {
        this.searchType = searchType;
    }

    private void generateCall() {
        switch (searchType) {
            case SEARCH_PHOTOS_WITH_TEXT: {
                call = Retro.getFlickrApi().getPhotosWithText(API_KEY, text, "photos", page);
                break;
            }
            case SEARCH_PHOTOS_WITH_GEO_COORDINATES: {
                call = Retro.getFlickrApi().getPhotosWithGeoCoordinates(API_KEY, latitude, longitude, "photos", page);
                text = "";
                break;
            }
            default:
                call = Retro.getFlickrApi().getPhotosWithText(API_KEY, text, "photos", page);
                break;
        }
    }

    @Override
    public void run() {
        Log.e(TAG, "Runnable.run : page " + page);
        generateCall();

        try {
            PhotoListResponse photoListResponse = call.execute().body();
            if (photoListResponse == null || !photoListResponse.getStat().equals(PhotoListResponse.STAT_OK)) {
                return;
            }

            final List<PhotoItemResponse> photos = photoListResponse.getPhotos().getPhoto();
            List<Photo> photoList = new ArrayList<>();

            for (int i = 0; i < photos.size(); i++) {
                photoList.add(new Photo(photos.get(i).getUrl(), text, photos.get(i).getId()));
            }

            if (page == 1) {
                pagesCount = photoListResponse.getPhotos().getPages();
            }
            photosFoundCallback.onPhotosFound(photoList, isUpdating, searchType);

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

}
