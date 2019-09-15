package com.my.bielik.task2.favourites;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.my.bielik.task2.R;
import com.my.bielik.task2.database.DBPhotoHelper;
import com.my.bielik.task2.database.object.PhotoItem;
import com.my.bielik.task2.main.MainActivity;
import com.my.bielik.task2.main.OnPhotoSelectedListener;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavouritesFragment extends Fragment {

    private RecyclerView rvFavourites;

    private DBPhotoHelper dbHelper;
    private FavouritesAdapter adapter;

    private OnPhotoSelectedListener photoSelectedListener;

    public FavouritesFragment() {
    }

    public static FavouritesFragment newInstance() {
        return new FavouritesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_favourites, container, false);

        rvFavourites = view.findViewById(R.id.recycler_view_favourites);
        dbHelper = new DBPhotoHelper(getActivity());

        setUpRecyclerView();
        updateFavouriteItemsList();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnPhotoSelectedListener) {
            photoSelectedListener = (OnPhotoSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString());
        }
    }

    private void setUpRecyclerView() {
        rvFavourites.setLayoutManager(new LinearLayoutManager(getActivity()));

        FavouritesAdapter.OnItemClickListener onItemClickListener = new FavouritesAdapter.OnItemClickListener() {
            @Override
            public void onClickListener(int position) {
                photoSelectedListener.onPhotoSelected(((Photo) adapter.getDataSet().get(position)).getSearchText(),
                        ((Photo) adapter.getDataSet().get(position)).getUrl(),
                        ((Photo) adapter.getDataSet().get(position)).getPhotoId());
            }
        };

        FavouritesAdapter.OnRemoveButtonClickListener onRemoveButtonClickListener = new FavouritesAdapter.OnRemoveButtonClickListener() {
            @Override
            public void onClickListener(int position) {
                removeItem(position);
            }
        };

        adapter = new FavouritesAdapter(onItemClickListener, onRemoveButtonClickListener);
        rvFavourites.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                removeItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(rvFavourites);
    }

    private void removeItem(int position) {
        if (adapter.getDataSet().get(position) instanceof Header) {
            adapter.notifyDataSetChanged();
            return;
        }
        PhotoItem photoItem = new PhotoItem(((Photo) adapter.getDataSet().get(position)).getSearchText(),
                ((Photo) adapter.getDataSet().get(position)).getUrl(), ((MainActivity) getActivity()).getUserId(), null);
        dbHelper.removeFavourite(photoItem);
        adapter.removeDateItem(position);
        adapter.notifyItemRemoved(position);

        if (dbHelper.getFavouritePhotoCount(photoItem) == 0) {
            if (--position != RecyclerView.NO_POSITION) {
                adapter.removeDateItem(position);
                adapter.notifyItemRemoved(position);
            }
        }
    }

    private void updateFavouriteItemsList() {
        adapter.updateDataSet(dbHelper, ((MainActivity) getActivity()).getUserId());
        if (adapter.getDataSet().size() == 0) {
            Toast.makeText(getActivity(), getString(R.string.toast_no_favourites), Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
    }
}
