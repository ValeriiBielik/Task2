package com.my.bielik.task2.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.my.bielik.task2.PhotoLoadWorker;
import com.my.bielik.task2.R;

import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import static com.my.bielik.task2.PhotoLoadWorker.PHOTO_LOAD_WORKER_TAG;
import static com.my.bielik.task2.settings.SettingsActivity.*;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.app_preferences, rootKey);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        CheckBoxPreference allowBackgroundUpdatesPreference = findPreference(KEY_PREF_ALLOW_BACKGROUND_UPDATES);
        allowBackgroundUpdatesPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                boolean selected = (boolean) o;

                if (selected) {
                    String repeatTimeInMinutes = sharedPreferences.getString(KEY_PREF_REPEAT_TIME, "15");
                    createPhotoLoadWorkerRequest(Integer.valueOf(repeatTimeInMinutes));
                    Toast.makeText(getActivity(), "selected", Toast.LENGTH_SHORT).show();
                } else {
                    WorkManager.getInstance().cancelAllWorkByTag(PHOTO_LOAD_WORKER_TAG);
                    Toast.makeText(getActivity(), "disabled", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        EditTextPreference requestTextPreference = findPreference(KEY_PREF_REQUEST_TEXT);
        requestTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Toast.makeText(getActivity(), (String) newValue, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        ListPreference repeatTimePreference = findPreference(KEY_PREF_REPEAT_TIME);
        repeatTimePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Toast.makeText(getActivity(), (String) newValue, Toast.LENGTH_SHORT).show();
                if (sharedPreferences.getBoolean(KEY_PREF_ALLOW_BACKGROUND_UPDATES, false)) {
                    updatePhotoLoadWorkerSettings(Integer.valueOf((String) newValue));
                }
                return true;
            }
        });


        Preference appThemePreference = findPreference(KEY_PREF_APP_THEME);
        appThemePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                boolean isDarkTheme = (boolean) o;
                if (isDarkTheme) {
                    AppCompatDelegate.setDefaultNightMode
                            (AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode
                            (AppCompatDelegate.MODE_NIGHT_NO);
                }
                return true;
            }
        });
    }

    private static void createPhotoLoadWorkerRequest(int repeatTimeInMinutes) {
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(PhotoLoadWorker.class, repeatTimeInMinutes, TimeUnit.MINUTES)
                .addTag(PHOTO_LOAD_WORKER_TAG)
                .setConstraints(constraints)
                .build();
            WorkManager.getInstance().enqueueUniquePeriodicWork(PHOTO_LOAD_WORKER_TAG, ExistingPeriodicWorkPolicy.KEEP, request);
    }

    private static void updatePhotoLoadWorkerSettings(int repeatTimeInMinutes) {
        WorkManager.getInstance().cancelAllWorkByTag(PHOTO_LOAD_WORKER_TAG);
        createPhotoLoadWorkerRequest(repeatTimeInMinutes);
    }
}
