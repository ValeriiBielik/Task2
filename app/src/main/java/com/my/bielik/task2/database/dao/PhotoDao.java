package com.my.bielik.task2.database.dao;

import com.my.bielik.task2.database.entity.Photo;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Photo photo);

    @Delete
    void delete(Photo photo);

    @Query("SELECT * FROM photo_table WHERE url = :url LIMIT 1")
    Photo findPhotoByUrl(String url);
}
