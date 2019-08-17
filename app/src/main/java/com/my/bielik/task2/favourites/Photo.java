package com.my.bielik.task2.favourites;

import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;

public class Photo implements RowType {

    private String url;
    private String searchText;

    public Photo(String url, String searchText) {
        this.url = url;
        this.searchText = searchText;
    }

    public String getUrl() {
        return url;
    }

    public String getSearchText() {
        return searchText;
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
