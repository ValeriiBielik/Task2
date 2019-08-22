package com.my.bielik.task2.favourites;

import androidx.recyclerview.widget.RecyclerView;

public class Header implements RowType {

    private String headerText;

    public Header(String headerText) {
        this.headerText = headerText;
    }

    @Override
    public int getItemViewType() {
        return HEADER_ROW_TYPE;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
        ViewHolderFactory.HeaderViewHolder headerViewHolder = (ViewHolderFactory.HeaderViewHolder) viewHolder;
        headerViewHolder.tvFavouritesHeader.setText(headerText);
    }
}
