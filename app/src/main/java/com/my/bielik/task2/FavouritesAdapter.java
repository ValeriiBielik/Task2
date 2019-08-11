package com.my.bielik.task2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.my.bielik.task2.database_objects.DatabasePhotoItem;

import java.util.ArrayList;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.FavouritesViewHolder> {

    private Context context;

    private ArrayList<DatabasePhotoItem> databasePhotoItems;

    public FavouritesAdapter(Context context, ArrayList<DatabasePhotoItem> databasePhotoItems) {
        this.context = context;
        this.databasePhotoItems = databasePhotoItems;
    }

    public class FavouritesViewHolder extends RecyclerView.ViewHolder {

        public TextView tvSearchTextItem;
        public TextView tvUrlItem;

        public FavouritesViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSearchTextItem = itemView.findViewById(R.id.tv_search_text_item);
            tvUrlItem = itemView.findViewById(R.id.tv_url_item);
        }

        public void bind(DatabasePhotoItem item) {
            tvSearchTextItem.setText(item.getSearchText());
            tvUrlItem.setMovementMethod(LinkMovementMethod.getInstance());
            tvUrlItem.setText(item.getSpannableUrl(), TextView.BufferType.SPANNABLE);
        }

    }

    @NonNull
    @Override
    public FavouritesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.favourite_photo_item, viewGroup, false);
        return new FavouritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouritesViewHolder favouritesViewHolder, int i) {
        favouritesViewHolder.bind(databasePhotoItems.get(i));
    }

    @Override
    public int getItemCount() {
        return databasePhotoItems.size();
    }
}
