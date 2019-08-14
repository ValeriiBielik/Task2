package com.my.bielik.task2.favourites;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class FavouritesAdapter extends RecyclerView.Adapter {

    private List<RowType> dataSet;
    private Photo.OnRemoveButtonClickListener onRemoveButtonClickListener;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClickListener(RecyclerView.ViewHolder viewHolder);
    }

    public FavouritesAdapter(List<RowType> dataSet) {
        this.dataSet = dataSet;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnRemoveButtonClickListener(Photo.OnRemoveButtonClickListener listener) {
        this.onRemoveButtonClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return ViewHolderFactory.create(viewGroup, i);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
        dataSet.get(i).onBindViewHolder(viewHolder);
        if (dataSet.get(i) instanceof Photo) {
            ((Photo) dataSet.get(i)).setOnRemoveButtonClickListener(onRemoveButtonClickListener, viewHolder);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClickListener(viewHolder);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return dataSet.get(position).getItemViewType();
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
