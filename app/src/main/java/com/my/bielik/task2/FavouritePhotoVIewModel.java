package com.my.bielik.task2;

import android.app.Application;

import com.my.bielik.task2.database.Database;
import com.my.bielik.task2.database.dao.FavouritePhotoDao;
import com.my.bielik.task2.database.dao.PhotoDao;
import com.my.bielik.task2.database.entity.FavouritePhoto;
import com.my.bielik.task2.database.entity.Photo;
import com.my.bielik.task2.favourites.Header;
import com.my.bielik.task2.favourites.RowType;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

public class FavouritePhotoVIewModel extends AndroidViewModel {

    private FavouritePhotoDao favouritePhotoDao;
    private PhotoDao photoDao;

    public FavouritePhotoVIewModel(@NonNull Application application) {
        super(application);
        favouritePhotoDao = Database.getInstance(application).favouritePhotoDao();
        photoDao = Database.getInstance(application).photoDao();
    }

    public long insertFavouritePhoto(Photo photo, int userId) {
        long id = photoDao.insert(photo);
        if (id == -1) {
            Photo oldPhoto = photoDao.findPhotoByUrl(photo.getUrl());
            FavouritePhoto favouritePhoto = favouritePhotoDao.findFavouritePhoto(oldPhoto.getId(), userId);
            if (favouritePhoto == null)
                return favouritePhotoDao.insert(new FavouritePhoto(oldPhoto.getId(), userId));
            else
                return -1;
        } else {
            return favouritePhotoDao.insert(new FavouritePhoto(id, userId));
        }
    }

    public int deleteFavouritePhoto(String url, int userId) {
        Photo photo = photoDao.findPhotoByUrl(url);
        int deletedRowsCount = 0;
        if (photo != null) {
            deletedRowsCount = favouritePhotoDao.delete(photo.getId(), userId);
            int count = favouritePhotoDao.getFavouritePhotosCountWithPhotoId(photo.getId());
            if (count == 0) {
                photoDao.delete(photo);
            }
        }
        return deletedRowsCount;
    }

    public LiveData<List<RowType>> getFavouritePhotos(int userId) {
        LiveData<List<Photo>> photos = favouritePhotoDao.getPhotosWithUserId(userId);
        final MediatorLiveData<List<RowType>> rowTypeListData = new MediatorLiveData<>();

        rowTypeListData.addSource(photos, new Observer<List<Photo>>() {
            @Override
            public void onChanged(List<Photo> photoList) {
                String previousTitle = "";
                List<RowType> items = new ArrayList<>();
                for (Photo photo : photoList) {
                    if (!previousTitle.equals(photo.getTitle())) {
                        previousTitle = photo.getTitle();
                        items.add(new Header(previousTitle));
                    }
                    items.add(photo);
                }
                rowTypeListData.setValue(items);
            }
        });

        return rowTypeListData;
    }

}
