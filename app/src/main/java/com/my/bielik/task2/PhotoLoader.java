package com.my.bielik.task2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.my.bielik.task2.database.object.PhotoItem;

public class PhotoLoader {

    public static final int PHOTO_ITEM_LOAD = 1;
    public static final int BITMAP_LOAD = 2;

    private static Bitmap bitmap;
    private static PhotoItem photoItem;
    private static int type;

    public static void createBitmap(String path) {
        bitmap = BitmapFactory.decodeFile(path);
        type = BITMAP_LOAD;
    }

    public static void setPhotoItem(PhotoItem photoItem) {
        PhotoLoader.photoItem = photoItem;
        type = PHOTO_ITEM_LOAD;
    }

    public static void setView(ImageView imageView) {
        switch (type) {
            case BITMAP_LOAD : {
                imageView.setImageBitmap(bitmap);
                break;
            }
            case PHOTO_ITEM_LOAD : {
                Glide.with(imageView.getContext()).load(photoItem.getUrl()).into(imageView);
                break;
            }
        }
    }

    public static int getType() {
        return type;
    }
}
