package com.my.bielik.task2.database.dao;

import com.my.bielik.task2.database.entity.LoadedPhoto;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface LoadedPhotoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(List<LoadedPhoto> photos);

    @Query("DELETE FROM loaded_photo_table")
    int deleteAllPhotos();

    @Query("SELECT * FROM loaded_photo_table")
    LiveData<List<LoadedPhoto>> getAllPhotos();
}
