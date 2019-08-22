package com.my.bielik.task2.thread;

import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.my.bielik.task2.R;
import com.my.bielik.task2.activity.MainActivity;
import com.my.bielik.task2.api.Retro;
import com.my.bielik.task2.api.response.FlickrResponse;
import com.my.bielik.task2.api.response.object.ResponsePhotoItem;
import com.my.bielik.task2.database.object.PhotoItem;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

import static com.my.bielik.task2.activity.LoginActivity.TAG;
import static com.my.bielik.task2.activity.MainActivity.API_KEY;

public class PhotoSearchRunnable implements Runnable {

    public static final int SEARCH_PHOTOS_WITH_TEXT = 1;
    public static final int SEARCH_PHOTOS_WITH_GEO_COORDINATES = 2;

    private int userId;
    private int page = 1;
    private int pagesCount;
    private boolean isUpdating;
    private String resultText;

    private int searchType;
    private String text;
    private double latitude;
    private double longitude;

    private WeakReference<MainActivity> activityWeakReference;
    private Call<FlickrResponse> call;

    public PhotoSearchRunnable(MainActivity activity, int userId) {
        activityWeakReference = new WeakReference<>(activity);
        this.userId = userId;
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
        resultText = null;
        page = 1;
    }

    public void setSearchType(int searchType) {
        this.searchType = searchType;
    }

    private void generateCall(MainActivity activity) {
        switch (searchType) {
            case SEARCH_PHOTOS_WITH_TEXT: {
                call = Retro.getFlickrApi().getPhotosWithText(API_KEY, text, "photos", page);
                resultText = text;
                break;
            }
            case SEARCH_PHOTOS_WITH_GEO_COORDINATES: {
                call = Retro.getFlickrApi().getPhotosWithGeoCoordinates(API_KEY, latitude, longitude, "photos", page);
                if (resultText == null) {
                    Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    } catch (IOException ex) {
                        resultText = "";
                        Log.e(TAG, "Service not available", ex);
                    } catch (IllegalArgumentException ex) {
                        resultText = "";
                        Log.e(TAG, "invalid latitude or longitude", ex);
                    }

                    if (addresses == null || addresses.size() == 0) {
                        resultText = "";
                    } else {
                        Address address = addresses.get(0);
                        StringBuilder result = new StringBuilder();
                        if (address.getLocality() != null) {
                            result.append(address.getLocality());
                        }
                        if (address.getCountryName() != null) {
                            if (result.length() != 0) {
                                result.append(", ");
                            }
                            result.append(address.getCountryName());
                        }
                        resultText = result.toString();
                    }
                }
                break;
            }

        }
    }

    @Override
    public void run() {
        Log.e(TAG, "Runnable.run : page " + page);
        final MainActivity activity = activityWeakReference.get();

        if (activity == null || activity.isFinishing()) {
            return;
        }
        Handler handler = new Handler(Looper.getMainLooper());
        generateCall(activity);
        try {
            FlickrResponse flickrResponse = call.execute().body();
            if (flickrResponse != null) {
                if (flickrResponse.getStat().equals(FlickrResponse.STAT_OK)) {
                    final List<ResponsePhotoItem> photos = flickrResponse.getPhotos().getPhoto();

                    if (!isUpdating) {
                        activity.getAdapter().clearDataSet();
                    }

                    for (int i = 0; i < photos.size(); i++) {
                        PhotoItem photoItem = new PhotoItem(resultText, photos.get(i).getUrl(), userId);
                        activity.getAdapter().updateDataSet(photoItem);
                    }

                    if (page == 1) {
                        pagesCount = flickrResponse.getPhotos().getPages();
                    }

                    Log.e(TAG, "last page: " + flickrResponse.getPhotos().getPages());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            activity.getAdapter().notifyDataSetChanged();
                            activity.finishLoading();
                            if (activity.getAdapter().getDataSet().size() == 0) {
                                Toast.makeText(activity, activity.getString(R.string.no_photos), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}

