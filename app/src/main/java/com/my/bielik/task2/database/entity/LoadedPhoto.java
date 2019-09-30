package com.my.bielik.task2.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "loaded_photo_table")
public class LoadedPhoto {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String url;

    @NonNull
    private String title;

    @NonNull
    private String flickrPhotoId;

    public LoadedPhoto(@NonNull String url, @NonNull String title, @NonNull String flickrPhotoId) {
        this.url = url;
        this.title = title;
        this.flickrPhotoId = flickrPhotoId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    @NonNull
    public String getFlickrPhotoId() {
        return flickrPhotoId;
    }

    @NonNull
    public String getTitle() {
        return title;
    }
}
