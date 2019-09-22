package com.my.bielik.task2;

import android.app.Application;
import android.util.Log;

import com.my.bielik.task2.database.Database;
import com.my.bielik.task2.database.dao.PhotoDao;
import com.my.bielik.task2.database.dao.RecentPhotoDao;
import com.my.bielik.task2.database.entity.Photo;
import com.my.bielik.task2.database.entity.RecentPhoto;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import static com.my.bielik.task2.user.LoginActivity.TAG;

public class RecentPhotoViewModel extends AndroidViewModel {

    private RecentPhotoDao recentPhotoDao;
    private PhotoDao photoDao;

    public RecentPhotoViewModel(@NonNull Application application) {
        super(application);
        recentPhotoDao = Database.getInstance(application).recentPhotoDao();
        photoDao = Database.getInstance(application).photoDao();
    }

    public void insertRecentPhoto(Photo photo, int userId) {
        long id = photoDao.insert(photo);
        if (id == -1) {
            Photo oldPhoto = photoDao.findPhotoByUrl(photo.getUrl());
            RecentPhoto recentPhoto = recentPhotoDao.findRecentPhoto(oldPhoto.getId(), userId);
            if (recentPhoto == null) {
                recentPhotoDao.insert(new RecentPhoto(oldPhoto.getId(), userId, System.currentTimeMillis()));
                deleteRecentPhoto(userId);
            } else {
                recentPhoto.setTimestamp(System.currentTimeMillis());
                recentPhotoDao.update(recentPhoto);
            }
        } else {
            recentPhotoDao.insert(new RecentPhoto(id, userId, System.currentTimeMillis()));
            deleteRecentPhoto(userId);
        }
    }

    private void deleteRecentPhoto(int userId) {
        if (recentPhotoDao.getRecentPhotosCountWithId(userId) > 20) {
            RecentPhoto recentPhoto = recentPhotoDao.getTheOldestPhoto(userId);
            Log.e(TAG, "deleted recent photos: " + recentPhotoDao.delete(recentPhoto));
        }
    }

    public LiveData<List<Photo>> getRecentPhotos(int userId) {
        return recentPhotoDao.getRecentPhotos(userId);
    }
}
