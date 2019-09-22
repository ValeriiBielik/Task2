package com.my.bielik.task2.database.dao;

import com.my.bielik.task2.database.entity.Photo;
import com.my.bielik.task2.database.entity.RecentPhoto;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface RecentPhotoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(RecentPhoto recentPhoto);

    @Update
    void update(RecentPhoto recentPhoto);

    @Query("DELETE FROM recent_photo_table WHERE photoId = :photoId AND userId = :userId")
    void delete(long photoId, int userId);

    @Delete
    int delete(RecentPhoto recentPhoto);

    @Query("SELECT * From recent_photo_table WHERE photoId = :photoId AND userId = :userId")
    RecentPhoto findRecentPhoto(long photoId, int userId);

    @Query("SELECT COUNT (*) FROM recent_photo_table WHERE userId = :userId")
    int getRecentPhotosCountWithId(int userId);

    @Query("SELECT * FROM recent_photo_table WHERE userId = :userId ORDER BY timestamp LIMIT 1")
    RecentPhoto getTheOldestPhoto(int userId);

    @Query("SELECT photo_table.id, photo_table.url, photo_table.title, photo_table.flickrPhotoId " +
            "FROM recent_photo_table " +
            "LEFT JOIN photo_table " +
            "ON recent_photo_table.photoId = photo_table.id " +
            "WHERE userId = :userId " +
            "ORDER BY timestamp desc")
    LiveData<List<Photo>> getRecentPhotos(int userId);

}
