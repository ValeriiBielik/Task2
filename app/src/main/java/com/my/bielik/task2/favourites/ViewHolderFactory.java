package com.my.bielik.task2.favourites;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.my.bielik.task2.R;

public class ViewHolderFactory {

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public TextView tvFavouritesHeader;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFavouritesHeader = itemView.findViewById(R.id.tv_favourites_header);
        }
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivPhoto;
        public ImageButton btnRemove;

        PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_favourite_photo);
            btnRemove = itemView.findViewById(R.id.btn_remove_item);
        }
    }

    @NonNull
    static RecyclerView.ViewHolder create(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        switch (viewType) {
            case RowType.HEADER_ROW_TYPE:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favourites_header, viewGroup, false);
                return new ViewHolderFactory.HeaderViewHolder(view);
            case RowType.PHOTO_ROW_TYPE:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favourite_photo_item, viewGroup, false);
                return new ViewHolderFactory.PhotoViewHolder(view);
            default:
                return null;
        }
    }
}
