package com.my.bielik.task2;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.my.bielik.task2.database.object.User;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {

    private List<User> usernameList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public UsersAdapter(List<User> usernameList) {
        this.usernameList = usernameList;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.username_item, viewGroup, false);
        return new UsersViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder usersViewHolder, int i) {
        usersViewHolder.tvUsername.setText(usernameList.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return usernameList.size();
    }

    static class UsersViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUsername;

        UsersViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

}
