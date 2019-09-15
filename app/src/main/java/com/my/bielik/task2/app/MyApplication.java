package com.my.bielik.task2.app;

import android.app.Application;
import android.content.SharedPreferences;

import com.facebook.stetho.Stetho;

import androidx.appcompat.app.AppCompatDelegate;

public class MyApplication extends Application {

    public static final String API_KEY = "539bab6327cc06a832b5793853bac293";

    public static final String APP_PREFERENCES = "app_prefs";
    public static final String APP_THEME = "app_theme";

    private static final int LIGHT_THEME = 0;
    private static final int DARK_THEME = 1;


    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.InitializerBuilder initializerBuilder = Stetho.newInitializerBuilder(this);
        initializerBuilder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this));
        initializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(this));
        Stetho.Initializer initializer =initializerBuilder.build();
        Stetho.initialize(initializer);

        SharedPreferences preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        if (preferences.contains(APP_THEME)) {
            if (preferences.getInt(APP_THEME, 0) == DARK_THEME) {
                AppCompatDelegate.setDefaultNightMode
                        (AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode
                        (AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }
}
