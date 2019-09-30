package com.my.bielik.task2.app;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.facebook.stetho.Stetho;

import androidx.appcompat.app.AppCompatDelegate;

public class MyApplication extends Application {

    public static final String API_KEY = "539bab6327cc06a832b5793853bac293";

    public static final String APP_PREFERENCES = "app_prefs";
    public static final String APP_THEME = "app_theme";

    public static final String PHOTO_LOAD_CHANNEL_ID = "photo_load_chanel";

    @Override
    public void onCreate() {
        super.onCreate();

        initializeStetho();
        setTheme();
        createNotificationChannel();

    }

    private void initializeStetho() {
        Stetho.InitializerBuilder initializerBuilder = Stetho.newInitializerBuilder(this);
        initializerBuilder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this));
        initializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(this));
        Stetho.Initializer initializer = initializerBuilder.build();
        Stetho.initialize(initializer);
    }

    private void setTheme() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (!sharedPreferences.getBoolean(APP_THEME, false)) {
            AppCompatDelegate.setDefaultNightMode
                    (AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode
                    (AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel photoLoadChannel = new NotificationChannel(
                    PHOTO_LOAD_CHANNEL_ID,
                    "Photo load channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            getSystemService(NotificationManager.class).createNotificationChannel(photoLoadChannel);
        }
    }


}
