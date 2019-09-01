package com.my.bielik.task2.user;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.my.bielik.task2.R;
import com.my.bielik.task2.database.DBPhotoHelper;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {

    private List<User> dataSet = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public UsersAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateDataSet(DBPhotoHelper dbHelper) {
        dbHelper.getUsers(dataSet);
    }

    public List<User> getDataSet() {
        return dataSet;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.username_item, viewGroup, false);
        final UsersViewHolder viewHolder = new UsersViewHolder(view);
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
    public void onBindViewHolder(@NonNull UsersViewHolder usersViewHolder, int i) {
        usersViewHolder.tvUsername.setText(dataSet.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    static class UsersViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUsername;

        UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
        }
    }

}
