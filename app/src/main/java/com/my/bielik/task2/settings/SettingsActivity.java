package com.my.bielik.task2.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.my.bielik.task2.R;

public class SettingsActivity extends AppCompatActivity {

    public static final String KEY_PREF_ALLOW_BACKGROUND_UPDATES = "allow_background_updates";
    public static final String KEY_PREF_REQUEST_TEXT = "request_text";
    public static final String KEY_PREF_REPEAT_TIME = "repeat_time";
    public static final String KEY_PREF_APP_THEME = "app_theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();
    }
}
