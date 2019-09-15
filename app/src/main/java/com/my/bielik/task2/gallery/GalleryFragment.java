package com.my.bielik.task2.gallery;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.my.bielik.task2.R;
import com.my.bielik.task2.main.OnPhotoSelectedListener;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class GalleryFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    private RecyclerView recyclerView;
    private GalleryAdapter adapter;

    private OnPhotoSelectedListener photoSelectedListener;

    public GalleryFragment() {
    }

    public static GalleryFragment newInstance() {
        return new GalleryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        recyclerView = view.findViewById(R.id.rv_gallery);
        setUpRecyclerView();
        updateGallery();

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
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        adapter = new GalleryAdapter();
        recyclerView.setAdapter(adapter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                deleteImage(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnImageClickListener(new GalleryAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(String path) {
                photoSelectedListener.onPhotoFromMemorySelected(path);
            }
        });
    }

    private void deleteImage(int position) {
        ImagesManager.getInstance().deleteImage(adapter.getImages().get(position).getSource());
        adapter.getImages().remove(position);
        adapter.notifyDataSetChanged();
    }


    private void updateGallery() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            new GalleryAsyncTask(this).execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new GalleryFragment.GalleryAsyncTask(this).execute();
            } else {
                Toast.makeText(getActivity(), getString(R.string.toast_required_write_external_storage_permission), Toast.LENGTH_SHORT).show();
            }
        }
    }

    static class GalleryAsyncTask extends AsyncTask<Void, Void, List<Image>> {

        private WeakReference<GalleryFragment> galleryFragmentWeakReference;

        GalleryAsyncTask(GalleryFragment fragment) {
            galleryFragmentWeakReference = new WeakReference<>(fragment);
        }

        @Override
        protected List<Image> doInBackground(Void... voids) {
            return ImagesManager.getInstance().getImages();
        }

        @Override
        protected void onPostExecute(List<Image> images) {
            super.onPostExecute(images);
            GalleryFragment fragment = galleryFragmentWeakReference.get();
            if (fragment != null) {
                fragment.adapter.setImages(images);
                fragment.adapter.notifyDataSetChanged();
            }
        }
    }

}
