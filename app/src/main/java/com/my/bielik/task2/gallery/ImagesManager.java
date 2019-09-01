package com.my.bielik.task2.gallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.common.util.ArrayUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImagesManager {

    private static final String TAG = "ImagesManager";
    private static final String PATH = "Task2";

    public static final int CREATE_PUBLIC_FILE = 1;
    public static final int CREATE_PRIVATE_FILE = 2;

    private static ImagesManager instance = new ImagesManager();
    private static final File privateStorage = new File(Environment.getExternalStorageDirectory(), PATH);
    private static File publicStorage = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), PATH);

    private ImagesManager() {
    }

    public static ImagesManager getInstance() {
        return instance;
    }

    public File createPrivateStorage() {
        if (!privateStorage.exists()) {
            if (!privateStorage.mkdirs()) {
                Log.e(TAG, "Could not create private storage directory: " + privateStorage.getAbsolutePath());
            }
        }
        return privateStorage;
    }

    public File createPublicStorage() {
        if (!publicStorage.exists()) {
            if (!publicStorage.mkdirs()) {
                Log.e(TAG, "Could not create public storage directory: " + publicStorage.getAbsolutePath());
            }
        }
        return publicStorage;
    }


    public void createImageFile(String title, Bitmap image, int fileType) throws IOException {
        File storage = fileType == CREATE_PRIVATE_FILE ? privateStorage : publicStorage;
        if (storage != null) {

            File file = new File(storage, createName(title));

            try (FileOutputStream fos = new FileOutputStream(file)) {
                image.compress(Bitmap.CompressFormat.PNG, 85, fos);
                Log.e(TAG, "File created");
            }
        }
    }

    public String createName(String title) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return title + "_" + timeStamp + ".png";
    }



    public void deleteImage(String fileName) {
        File file = new File(fileName);
        if (!file.delete()) {
            Log.e(TAG, "File could not be deleted: " + fileName);
        }
    }

    public List<Image> getImages() {
        File[] files = ArrayUtils.concat(publicStorage.listFiles(), privateStorage.listFiles());
        if (files == null) {
            Log.e(TAG, "Could not list files.");
            return null;
        }
        ArrayList<Image> list = new ArrayList<>(files.length);
        for (File f : files) {
            Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
            list.add(new Image(f.getAbsolutePath(), bitmap));
        }
        return list;
    }
}
