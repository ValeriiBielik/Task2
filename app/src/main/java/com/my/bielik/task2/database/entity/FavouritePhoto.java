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
    private int userID;

    public FavouritePhoto(long photoId, int userID) {
        this.userID = userID;
        this.photoId = photoId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserID() {
        return userID;
    }

    public long getPhotoId() {
        return photoId;
    }
}
