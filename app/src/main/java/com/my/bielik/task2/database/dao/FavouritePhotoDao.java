package com.my.bielik.task2.database.dao;

import com.my.bielik.task2.database.entity.FavouritePhoto;
import com.my.bielik.task2.database.entity.Photo;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface FavouritePhotoDao {

    @Query("SELECT * From favourite_photos_table WHERE photoId = :photoId AND userID = :userId")
    FavouritePhoto findFavouritePhoto(long photoId, int userId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(FavouritePhoto favouritePhoto);

    @Query("DELETE FROM favourite_photos_table WHERE photoId = :photoId AND userID = :userId")
    int delete(long photoId, int userId);

    @Query("SELECT COUNT (*) FROM favourite_photos_table WHERE photoId = :photoId")
    int getFavouritePhotosCountWithPhotoId(int photoId);

    @Query("SELECT photo_table.id, photo_table.url, photo_table.title, photo_table.flickrPhotoId " +
            "FROM favourite_photos_table " +
            "LEFT JOIN photo_table " +
            "ON favourite_photos_table.photoId = photo_table.id " +
            "WHERE userID = :userId " +
            "ORDER BY title")
    LiveData<List<Photo>> getPhotosWithUserId(int userId);

}
