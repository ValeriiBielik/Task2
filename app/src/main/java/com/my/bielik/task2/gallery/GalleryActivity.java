package com.my.bielik.task2.gallery;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.my.bielik.task2.R;
import com.my.bielik.task2.photoview.PhotoActivity;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import static com.my.bielik.task2.photoview.PhotoActivity.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;

public class GalleryActivity extends AppCompatActivity {

    private static final String TAG = "GalleryActivity";

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private RecyclerView recyclerView;
    private GalleryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        recyclerView = findViewById(R.id.rv_gallery);
        setUpRecyclerView();

        updateGallery();
    }

    private void setUpRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
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
                Intent intent = new Intent(GalleryActivity.this, PhotoActivity.class);
                intent.setType("path");
                intent.putExtra("path_extra", path);
                startActivity(intent);
            }
        });

    }

    private void deleteImage(int position) {
        ImagesManager.getInstance().deleteImage(adapter.getImages().get(position).getSource());
        adapter.getImages().remove(position);
        adapter.notifyDataSetChanged();
    }

    public void openCamera(View view) {
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data == null) {
                Log.e(TAG, "data is null");
            } else {
                Bundle bundle = data.getExtras();
                Bitmap imageBitmap = (Bitmap) bundle.get("data");
                startCrop(imageBitmap);
            }
        }
    }

    private void updateGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
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
                new GalleryAsyncTask(this).execute();
            } else {
                Toast.makeText(this, getString(R.string.toast_required_write_external_storage_permission), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateGallery();
    }

    private void startCrop(Bitmap bitmap) {
        UCrop.of(getImageUri(this, bitmap),
                Uri.fromFile(new File(ImagesManager.getInstance().createPrivateStorage(),
                        ImagesManager.getInstance().createName("Png"))))
                .withAspectRatio(1, 1)
                .withMaxResultSize(450, 450)
                .start(this);

    }

    public Uri getImageUri(Context context, Bitmap image) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), image, "Title", null);
        return Uri.parse(path);
    }

    static class GalleryAsyncTask extends AsyncTask<Void, Void, List<Image>> {

        private WeakReference<GalleryActivity> galleryActivityWeakReference;

        GalleryAsyncTask(GalleryActivity activity) {
            galleryActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected List<Image> doInBackground(Void... voids) {
            return ImagesManager.getInstance().getImages();
        }

        @Override
        protected void onPostExecute(List<Image> images) {
            super.onPostExecute(images);
            GalleryActivity activity = galleryActivityWeakReference.get();
            if (activity != null && !activity.isFinishing()) {
                activity.adapter.setImages(images);
                activity.adapter.notifyDataSetChanged();
            }
        }
    }
}
