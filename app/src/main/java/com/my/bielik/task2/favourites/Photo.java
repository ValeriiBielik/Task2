package com.my.bielik.task2.favourites;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

public class Photo implements RowType {

    private String url;
    private String searchText;
    private String photoId;

    public Photo(String url, String searchText, String photoId) {
        this.url = url;
        this.searchText = searchText;
        this.photoId = photoId;
    }

    public String getUrl() {
        return url;
    }

    public String getSearchText() {
        return searchText;
    }

    public String getPhotoId() {
        return photoId;
    }

    @Override
    public int getItemViewType() {
        return PHOTO_ROW_TYPE;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder) {
        ViewHolderFactory.PhotoViewHolder photoViewHolder = (ViewHolderFactory.PhotoViewHolder) viewHolder;
        Glide.with(viewHolder.itemView.getContext())
                .load(url)
                .into(photoViewHolder.ivPhoto);
    }
}
