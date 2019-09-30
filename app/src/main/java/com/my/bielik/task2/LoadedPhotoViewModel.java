package com.my.bielik.task2;

import android.app.Application;
import android.util.Log;

import com.my.bielik.task2.database.Database;
import com.my.bielik.task2.database.dao.LoadedPhotoDao;
import com.my.bielik.task2.database.entity.LoadedPhoto;
import com.my.bielik.task2.database.entity.Photo;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import static com.my.bielik.task2.user.LoginActivity.TAG;

public class LoadedPhotoViewModel extends AndroidViewModel {

    private LoadedPhotoDao loadedPhotoDao;
    private LiveData<List<LoadedPhoto>> allPhotos;

    public LoadedPhotoViewModel(@NonNull Application application) {
        super(application);
        loadedPhotoDao = Database.getInstance(application).loadedPhotoDao();
        allPhotos = loadedPhotoDao.getAllPhotos();
    }

    public LiveData<List<Photo>> getAllPhotos() {
        final MediatorLiveData<List<Photo>> resultPhotos = new MediatorLiveData<>();
        resultPhotos.addSource(allPhotos, new Observer<List<LoadedPhoto>>() {
            @Override
            public void onChanged(List<LoadedPhoto> photos) {
                List<Photo> photoList = new ArrayList<>();
                for (LoadedPhoto loadedPhoto : photos) {
                    photoList.add(new Photo(loadedPhoto.getUrl(), loadedPhoto.getTitle(), loadedPhoto.getFlickrPhotoId()));
                }
                resultPhotos.setValue(photoList);
            }
        });
        return resultPhotos;
    }
}
