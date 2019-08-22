package com.my.bielik.task2.favourites;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.my.bielik.task2.database.PhotosDBHelper;

import java.util.ArrayList;
import java.util.List;

public class FavouritesAdapter extends RecyclerView.Adapter {

    private List<RowType> dataSet = new ArrayList<>();
    private OnRemoveButtonClickListener onRemoveButtonClickListener;
    private OnItemClickListener onItemClickListener;

    public FavouritesAdapter(OnItemClickListener itemClickListener, OnRemoveButtonClickListener removeButtonClickListener) {
        this.onItemClickListener = itemClickListener;
        this.onRemoveButtonClickListener = removeButtonClickListener;
    }

    public void updateDataSet(PhotosDBHelper dbHelper, int userId) {
        dbHelper.getFavouritePhotos(dataSet, userId);
    }

    public void removeDateItem(int position) {
        dataSet.remove(position);
    }

    public List<RowType> getDataSet() {
        return dataSet;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final RecyclerView.ViewHolder viewHolder = ViewHolderFactory.create(viewGroup, i);
        if (i == RowType.PHOTO_ROW_TYPE) {

            ((ViewHolderFactory.PhotoViewHolder) viewHolder).btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onRemoveButtonClickListener != null) {
                        final int position = viewHolder.getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onRemoveButtonClickListener.onClickListener(position);
                        }
                    }
                }
            });

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        final int position = viewHolder.getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onClickListener(position);
                        }
                    }
                }
            });
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
        dataSet.get(i).onBindViewHolder(viewHolder);
    }

    @Override
    public int getItemViewType(int position) {
        return dataSet.get(position).getItemViewType();
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnRemoveButtonClickListener {
        void onClickListener(int position);
    }

    public interface OnItemClickListener {
        void onClickListener(int position);
    }
}
