package com.my.bielik.task2.loaded;


import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.my.bielik.task2.LoadedPhotoViewModel;
import com.my.bielik.task2.R;
import com.my.bielik.task2.database.entity.LoadedPhoto;
import com.my.bielik.task2.database.entity.Photo;
import com.my.bielik.task2.main.OnPhotoSelectedListener;
import com.my.bielik.task2.main.PhotoAdapter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.my.bielik.task2.settings.SettingsActivity.KEY_PREF_REQUEST_TEXT;

public class LoadedPhotoFragment extends Fragment {

    private OnPhotoSelectedListener onPhotoSelectedListener;
    private LoadedPhotoViewModel loadedPhotoViewModel;

    private RecyclerView recyclerView;
    private PhotoAdapter adapter;

    public LoadedPhotoFragment() {
    }

    public static LoadedPhotoFragment newInstance() {
        return new LoadedPhotoFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnPhotoSelectedListener) {
            onPhotoSelectedListener = (OnPhotoSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadedPhotoViewModel = ViewModelProviders.of(this).get(LoadedPhotoViewModel.class);
        loadedPhotoViewModel.getAllPhotos().observe(this, new Observer<List<Photo>>() {
            @Override
            public void onChanged(List<Photo> photos) {
                adapter.setDataSet(photos);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loaded_photo, container, false);

        recyclerView = view.findViewById(R.id.rv_loaded_photos);

        setUpRecycleView();
        return view;
    }

    private void setUpRecycleView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PhotoAdapter(new PhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String title = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(KEY_PREF_REQUEST_TEXT, "");
                onPhotoSelectedListener.onPhotoSelected(title, adapter.getDataSet().get(position).getUrl(),
                        adapter.getDataSet().get(position).getFlickrPhotoId());
            }
        });
        recyclerView.setAdapter(adapter);
    }

}
