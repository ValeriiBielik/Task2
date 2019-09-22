package com.my.bielik.task2.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recent_photo_table")
public class RecentPhoto {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private long photoId;

    @NonNull
    private int userId;

    @NonNull
    private long timestamp;

    public RecentPhoto(long photoId, int userId, long timestamp) {
        this.photoId = photoId;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getPhotoId() {
        return photoId;
    }

    public int getUserId() {
        return userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
