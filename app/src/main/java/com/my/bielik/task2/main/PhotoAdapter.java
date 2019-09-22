package com.my.bielik.task2.main;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.my.bielik.task2.R;
import com.my.bielik.task2.database.entity.Photo;

import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private List<Photo> dataSet = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public PhotoAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setDataSet(List<Photo> dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }

    void removeDataItem(int position) {
        dataSet.remove(position);
    }

    void clearDataSet() {
        dataSet.clear();
    }

    public List<Photo> getDataSet() {
        return dataSet;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.photo_item, viewGroup, false);
        final PhotoViewHolder viewHolder = new PhotoViewHolder(view);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    int position = viewHolder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PhotoViewHolder photoViewHolder, int i) {
        photoViewHolder.tvRequestText.setText(dataSet.get(i).getTitle());
        Glide.with(photoViewHolder.itemView.getContext()).
                load(dataSet.get(i).getUrl()).
                into(photoViewHolder.ivPhoto);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivPhoto;
        private TextView tvRequestText;

        PhotoViewHolder(@NonNull View itemView) {
            super(itemView);

            ivPhoto = itemView.findViewById(R.id.iv_photo);
            tvRequestText = itemView.findViewById(R.id.tv_request_text);
        }
    }
}
