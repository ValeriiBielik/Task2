package com.my.bielik.task2;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.my.bielik.task2.database.object.PhotoItem;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private List<PhotoItem> photoItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(RecyclerView.ViewHolder viewHolder);
    }

    public PhotoAdapter(List<PhotoItem> photoItems) {
        this.photoItems = photoItems;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.photo_item, viewGroup, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PhotoViewHolder photoViewHolder, int i) {
        photoViewHolder.tvRequestText.setText(photoItems.get(i).getSearchText());
        Glide.with(photoViewHolder.itemView.getContext()).
                load(photoItems.get(i).getUrl()).
                into(photoViewHolder.ivPhoto);

        photoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(photoViewHolder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return photoItems.size();
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
