package com.my.bielik.task2.favourites;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.my.bielik.task2.FavouritePhotoViewModel;
import com.my.bielik.task2.R;
import com.my.bielik.task2.database.entity.Photo;
import com.my.bielik.task2.main.MainActivity;
import com.my.bielik.task2.main.OnPhotoSelectedListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavouritesFragment extends Fragment {

    private RecyclerView rvFavourites;

    private FavouritesAdapter adapter;
    private FavouritePhotoViewModel favouritePhotoVIewModel;

    private OnPhotoSelectedListener photoSelectedListener;

    public FavouritesFragment() {
    }

    public static FavouritesFragment newInstance() {
        return new FavouritesFragment();
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favouritePhotoVIewModel = ViewModelProviders.of(this).get(FavouritePhotoViewModel.class);
        favouritePhotoVIewModel.getFavouritePhotos(((MainActivity) getActivity()).getUserId()).observe(this, new Observer<List<RowType>>() {
            @Override
            public void onChanged(List<RowType> rowTypes) {
                adapter.setDataSet(rowTypes);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        rvFavourites = view.findViewById(R.id.recycler_view_favourites);
        setUpRecyclerView();
        return view;
    }

    private void setUpRecyclerView() {
        rvFavourites.setLayoutManager(new LinearLayoutManager(getActivity()));

        FavouritesAdapter.OnItemClickListener onItemClickListener = new FavouritesAdapter.OnItemClickListener() {
            @Override
            public void onClickListener(int position) {
                photoSelectedListener.onPhotoSelected(((Photo) adapter.getDataSet().get(position)).getTitle(),
                        ((Photo) adapter.getDataSet().get(position)).getUrl(),
                        ((Photo) adapter.getDataSet().get(position)).getFlickrPhotoId());
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

    private void removeItem(final int position) {
        if (adapter.getDataSet().get(position) instanceof Header) {
            adapter.notifyDataSetChanged();
            return;
        }
        ((MainActivity) getActivity()).getProcessResponseThread().getHandler().post(new Runnable() {
            @Override
            public void run() {
                favouritePhotoVIewModel.deleteFavouritePhoto(((Photo) adapter.getDataSet().get(position)).getUrl(),
                        ((MainActivity) getActivity()).getUserId());
            }
        });
    }

}
