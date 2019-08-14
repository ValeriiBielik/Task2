package com.my.bielik.task2.favourites;

import android.support.v7.widget.RecyclerView;

public interface RowType {
    int HEADER_ROW_TYPE = 0;
    int PHOTO_ROW_TYPE = 1;

    int getItemViewType();
    void onBindViewHolder(RecyclerView.ViewHolder viewHolder);
}
