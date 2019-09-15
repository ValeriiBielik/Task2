package com.my.bielik.task2.recent;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.my.bielik.task2.R;
import com.my.bielik.task2.database.DBPhotoHelper;
import com.my.bielik.task2.main.MainActivity;
import com.my.bielik.task2.main.OnPhotoSelectedListener;
import com.my.bielik.task2.main.PhotoAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecentFragment extends androidx.fragment.app.Fragment {

    private RecyclerView rvRecentPhotos;
    private PhotoAdapter adapter;
    private DBPhotoHelper dbHelper;

    private OnPhotoSelectedListener photoSelectedListener;

    public RecentFragment() {
    }

    public static RecentFragment newInstance() {
        return new RecentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recent, container, false);
        rvRecentPhotos = view.findViewById(R.id.rv_recent_photos);

        dbHelper = new DBPhotoHelper(getActivity());

        setUpRecyclerView();
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
        rvRecentPhotos.setLayoutManager(new LinearLayoutManager(getActivity()));

        PhotoAdapter.OnItemClickListener listener = new PhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                photoSelectedListener.onPhotoSelected(adapter.getDataSet().get(position).getSearchText(),
                        adapter.getDataSet().get(position).getUrl(),
                        adapter.getDataSet().get(position).getPhotoId());
            }
        };

        adapter = new PhotoAdapter(listener);
        rvRecentPhotos.setAdapter(adapter);

        updateDataSet();
    }

    private void updateDataSet() {
        adapter.updateDataSetWithDB(dbHelper, ((MainActivity) getActivity()).getUserId());
        adapter.notifyDataSetChanged();
    }

}
