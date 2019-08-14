package com.my.bielik.task2.favourites;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;

public class Photo implements RowType {

    private String url;

    private OnRemoveButtonClickListener listener;
    private RecyclerView.ViewHolder viewHolder;
    private String searchText;

    public Photo(String url, String searchText) {
        this.url = url;
        this.searchText = searchText;
    }

    public interface OnRemoveButtonClickListener {
        void onClickListener(RecyclerView.ViewHolder viewHolder);
    }

    public String getUrl() {
        return url;
    }

    public String getSearchText() {
        return searchText;
    }

    void setOnRemoveButtonClickListener(OnRemoveButtonClickListener listener, RecyclerView.ViewHolder viewHolder){
        this.listener = listener;
        this.viewHolder = viewHolder;
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
        photoViewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickListener(viewHolder);
            }
        });
    }
}
