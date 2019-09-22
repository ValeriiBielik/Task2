package com.my.bielik.task2.recent;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.my.bielik.task2.R;
import com.my.bielik.task2.RecentPhotoViewModel;
import com.my.bielik.task2.database.entity.Photo;
import com.my.bielik.task2.main.MainActivity;
import com.my.bielik.task2.main.OnPhotoSelectedListener;
import com.my.bielik.task2.main.PhotoAdapter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecentFragment extends androidx.fragment.app.Fragment {

    private RecyclerView rvRecentPhotos;
    private PhotoAdapter adapter;
    private RecentPhotoViewModel recentPhotoViewModel;

    private OnPhotoSelectedListener photoSelectedListener;

    public RecentFragment() {
    }

    public static RecentFragment newInstance() {
        return new RecentFragment();
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
        recentPhotoViewModel = ViewModelProviders.of(this).get(RecentPhotoViewModel.class);
        recentPhotoViewModel.getRecentPhotos(((MainActivity) getActivity()).getUserId()).observe(this, new Observer<List<Photo>>() {
            @Override
            public void onChanged(List<Photo> photos) {
                adapter.setDataSet(photos);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recent, container, false);
        rvRecentPhotos = view.findViewById(R.id.rv_recent_photos);
        setUpRecyclerView();
        return view;
    }

    private void setUpRecyclerView() {
        rvRecentPhotos.setLayoutManager(new LinearLayoutManager(getActivity()));

        PhotoAdapter.OnItemClickListener listener = new PhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                photoSelectedListener.onPhotoSelected(adapter.getDataSet().get(position).getTitle(),
                        adapter.getDataSet().get(position).getUrl(),
                        adapter.getDataSet().get(position).getFlickrPhotoId());
            }
        };

        adapter = new PhotoAdapter(listener);
        rvRecentPhotos.setAdapter(adapter);
    }
}
