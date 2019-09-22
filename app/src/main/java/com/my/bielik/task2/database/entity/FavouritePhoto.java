package com.my.bielik.task2.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favourite_photos_table")
public class FavouritePhoto {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private long photoId;

    @NonNull
    private int userId;

    public FavouritePhoto(long photoId, int userId) {
        this.userId = userId;
        this.photoId = photoId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public long getPhotoId() {
        return photoId;
    }
}
