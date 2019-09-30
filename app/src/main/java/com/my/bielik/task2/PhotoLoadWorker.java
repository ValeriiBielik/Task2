package com.my.bielik.task2;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.my.bielik.task2.api.Retro;
import com.my.bielik.task2.api.response.object.PhotoItemResponse;
import com.my.bielik.task2.api.response.object.PhotoListResponse;
import com.my.bielik.task2.database.Database;
import com.my.bielik.task2.database.entity.LoadedPhoto;
import com.my.bielik.task2.database.entity.Photo;
import com.my.bielik.task2.main.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import retrofit2.Call;

import static com.my.bielik.task2.app.MyApplication.API_KEY;
import static com.my.bielik.task2.app.MyApplication.PHOTO_LOAD_CHANNEL_ID;
import static com.my.bielik.task2.settings.SettingsActivity.KEY_PREF_ALLOW_BACKGROUND_UPDATES;
import static com.my.bielik.task2.settings.SettingsActivity.KEY_PREF_REQUEST_TEXT;
import static com.my.bielik.task2.user.LoginActivity.TAG;

public class PhotoLoadWorker extends Worker {

    public static final String PHOTO_LOAD_WORKER_TAG = "photo_load";
    public static final String KEY_NOTIFICATION = "notification";

    private SharedPreferences appPreferences;
    private Database database;
    private Context context;

    public PhotoLoadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        appPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        database = Database.getInstance(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (appPreferences.getBoolean(KEY_PREF_ALLOW_BACKGROUND_UPDATES, false)) {
            Log.e(TAG, "Worker: work start");
            String title = appPreferences.getString(KEY_PREF_REQUEST_TEXT, "");

            if (title.equals("")) {
                return Result.failure();
            }

            Call<PhotoListResponse> call = Retro.getFlickrApi().getPhotosWithText(API_KEY, title, "photos", 1);
            try {
                PhotoListResponse photoListResponse = call.execute().body();
                if (photoListResponse == null || !photoListResponse.getStat().equals(PhotoListResponse.STAT_OK)) {
                    return Result.failure();
                }
                final List<PhotoItemResponse> responseList = photoListResponse.getPhotos().getPhoto();
                List<LoadedPhoto> photoList = new ArrayList<>();
                for (PhotoItemResponse photoItemResponse : responseList) {
                    photoList.add(new LoadedPhoto(photoItemResponse.getUrl(), title, photoItemResponse.getId()));
                }

                database.loadedPhotoDao().deleteAllPhotos();
                database.loadedPhotoDao().insert(photoList);

                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(KEY_NOTIFICATION, PHOTO_LOAD_WORKER_TAG);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                Notification notification = new NotificationCompat.Builder(context, PHOTO_LOAD_CHANNEL_ID)
                        .setContentTitle("Photos loaded")
                        .setContentText("with request: " + title + ", count: " + photoList.size())
                        .setCategory(Notification.CATEGORY_MESSAGE)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.photo)
                        .setAutoCancel(true)
                        .build();

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(1, notification);

            } catch (IOException ex) {
                Log.e(TAG, ex.getMessage());
            }

        }
        Log.e(TAG, "Worker: work end");
        return Result.success();
    }
}
