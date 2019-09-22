package com.my.bielik.task2.database.entity;

import com.bumptech.glide.Glide;
import com.my.bielik.task2.favourites.RowType;
import com.my.bielik.task2.favourites.ViewHolderFactory;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "photo_table",
        indices = {@Index(value = "url", unique = true),
                @Index(value = "flickrPhotoId", unique = true)})
public class Photo implements RowType {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String url;

    @NonNull
    private String title;

    @NonNull
    private String flickrPhotoId;

    public Photo(@NonNull String url, @NonNull String title, @NonNull String flickrPhotoId) {
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

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public String getFlickrPhotoId() {
        return flickrPhotoId;
    }

    @Override
    public int getItemViewType() {
        return PHOTO_ROW_TYPE;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
        ViewHolderFactory.PhotoViewHolder photoViewHolder = (ViewHolderFactory.PhotoViewHolder) viewHolder;
        Glide.with(viewHolder.itemView.getContext())
                .load(url)
                .into(photoViewHolder.ivPhoto);
    }
}
