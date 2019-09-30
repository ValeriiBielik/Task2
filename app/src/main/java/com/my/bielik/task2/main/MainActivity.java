package com.my.bielik.task2.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.my.bielik.task2.R;
import com.my.bielik.task2.favourites.FavouritesFragment;
import com.my.bielik.task2.gallery.GalleryFragment;
import com.my.bielik.task2.gallery.ImagesManager;
import com.my.bielik.task2.loaded.LoadedPhotoFragment;
import com.my.bielik.task2.map.MapFragment;
import com.my.bielik.task2.photoview.PhotoViewFragment;
import com.my.bielik.task2.recent.RecentFragment;
import com.my.bielik.task2.thread.ProcessResponseThread;
import com.my.bielik.task2.user.LoginActivity;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import static com.my.bielik.task2.PhotoLoadWorker.KEY_NOTIFICATION;
import static com.my.bielik.task2.PhotoLoadWorker.PHOTO_LOAD_WORKER_TAG;
import static com.my.bielik.task2.user.LoginActivity.LATITUDE_EXTRA;
import static com.my.bielik.task2.user.LoginActivity.LONGITUDE_EXTRA;
import static com.my.bielik.task2.user.LoginActivity.PHOTO_ID_EXTRA;
import static com.my.bielik.task2.user.LoginActivity.SEARCH_TEXT_EXTRA;
import static com.my.bielik.task2.user.LoginActivity.URL_EXTRA;
import static com.my.bielik.task2.user.LoginActivity.USER_ID_EXTRA;

public class MainActivity extends AppCompatActivity implements MapFragment.OnPlaceSelectedCallback, OnPhotoSelectedListener {

    public static final String LAST_SEARCH_VALUE = "last_search_value";
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private ProcessResponseThread processResponseThread = new ProcessResponseThread();

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private BroadcastReceiver batteryLevelReceiver;

    private int userId;
    private int batteryLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        nvDrawer = findViewById(R.id.nvView);

        if (savedInstanceState != null) {
            batteryLevel = savedInstanceState.getInt("battery_level");
        }

        if (getIntent() != null) {
            if (getIntent().getStringExtra(KEY_NOTIFICATION) != null && getIntent().getStringExtra(KEY_NOTIFICATION).equals(PHOTO_LOAD_WORKER_TAG)) {
                getSupportFragmentManager().beginTransaction().replace(R.id.flContent, LoadedPhotoFragment.newInstance()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.flContent, PhotoSearchFragment.newInstance()).commit();
            }
            userId = getIntent().getIntExtra(USER_ID_EXTRA, 1);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupDrawerContent(nvDrawer);

        drawerToggle = setupDrawerToggle();
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();

        drawer.addDrawerListener(drawerToggle);

        processResponseThread.start();

        setBatteryLevelReceiver();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("battery_level", batteryLevel);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public void onPhotoSelected(String title, String url, String photoId) {
        Fragment fragment = new PhotoViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SEARCH_TEXT_EXTRA, title);
        bundle.putString(URL_EXTRA, url);
        bundle.putInt(USER_ID_EXTRA, userId);
        bundle.putString(PHOTO_ID_EXTRA, photoId);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onPhotoFromMemorySelected(String path) {
        Fragment fragment = new PhotoViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("path_extra", path);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gallery_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.take_photo) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
            return true;
        }

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
    }

    private void selectDrawerItem(MenuItem item) {
        Fragment fragment;
        switch (item.getItemId()) {
            case R.id.nav_gallery:
                fragment = GalleryFragment.newInstance();
                toolbar.setTitle(R.string.menu_title_gallery);
                break;
            case R.id.nav_favourites:
                fragment = FavouritesFragment.newInstance();
                toolbar.setTitle(R.string.menu_title_favourites_photos);
                break;
            case R.id.nav_recent_photos:
                fragment = RecentFragment.newInstance();
                toolbar.setTitle(R.string.menu_title_resent_photos);
                break;
            case R.id.nav_map:
                fragment = MapFragment.newInstance();
                toolbar.setTitle(R.string.menu_title_map);
                break;
            case R.id.nav_loaded_photos:
                fragment = LoadedPhotoFragment.newInstance();
                toolbar.setTitle(getString(R.string.menu_title_loaded_photos));
                break;
            case R.id.nav_photo_search:
            default:
                fragment = PhotoSearchFragment.newInstance();
                toolbar.setTitle(R.string.menu_title_search_photos);
        }
        drawer.closeDrawers();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
//        item.setChecked(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data == null) {
                Log.e("MainActivity", "data is null");
            } else {
                Bundle bundle = data.getExtras();
                Bitmap imageBitmap = (Bitmap) bundle.get("data");
                startCrop(imageBitmap);
            }
        }
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

    public int getUserId() {
        return userId;
    }

    public ProcessResponseThread getProcessResponseThread() {
        return processResponseThread;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        processResponseThread.quit();
        unregisterReceiver(batteryLevelReceiver);
    }

    @Override
    public void onPlaceSelected(double latitude, double longitude) {
        PhotoSearchFragment fragment = new PhotoSearchFragment();
        Bundle arguments = new Bundle();
        arguments.putDouble(LATITUDE_EXTRA, latitude);
        arguments.putDouble(LONGITUDE_EXTRA, longitude);
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    private void setBatteryLevelReceiver() {
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        batteryLevelReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

                if (batteryLevel != level) {
                    batteryLevel = level;
                    Toast.makeText(context, "Battery level " + batteryLevel, Toast.LENGTH_SHORT).show();
                    Log.e(LoginActivity.TAG, "Battery level changed: " + batteryLevel);
                }
            }
        };
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
    }
}
