package com.my.bielik.task2.user;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.my.bielik.task2.R;
import com.my.bielik.task2.main.MainActivity;
import com.my.bielik.task2.database.DBPhotoHelper;

import static com.my.bielik.task2.app.MyApplication.APP_PREFERENCES;
import static com.my.bielik.task2.app.MyApplication.APP_THEME;

public class LoginActivity extends AppCompatActivity {

    public static final String USER_ID_EXTRA = "userId";
    public static final String PHOTO_ID_EXTRA = "photoId";
    public static final String URL_EXTRA = "url";
    public static final String SEARCH_TEXT_EXTRA = "search_text";
    public static final String LATITUDE_EXTRA = "latitude";
    public static final String LONGITUDE_EXTRA = "longitude";

    public static final String TAG = "PhotoApp";

    private EditText etUsername;
    private RecyclerView rvUsers;

    private DBPhotoHelper dbHelper;
    private UsersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_username);
        rvUsers = findViewById(R.id.rv_users);

        dbHelper = new DBPhotoHelper(this);

        Toolbar toolbar = findViewById(R.id.settings_bar);
        setSupportActionBar(toolbar);

        setUpRecyclerView();
        updateUserList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            menu.findItem(R.id.theme).setTitle(R.string.theme_light);
        } else {
            menu.findItem(R.id.theme).setTitle(R.string.theme_dark);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences.Editor editor = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE).edit();
        if (item.getItemId() == R.id.theme) {
            int nightMode = AppCompatDelegate.getDefaultNightMode();
            if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode
                        (AppCompatDelegate.MODE_NIGHT_NO);
                editor.putInt(APP_THEME, 0);
            } else {
                AppCompatDelegate.setDefaultNightMode
                        (AppCompatDelegate.MODE_NIGHT_YES);
                editor.putInt(APP_THEME, 1);
            }
        }
        editor.apply();
        recreate();

        return true;
    }

    public void setUpRecyclerView() {
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        UsersAdapter.OnItemClickListener listener = new UsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                login(adapter.getDataSet().get(position).getId());
            }
        };
        adapter = new UsersAdapter(listener);
        rvUsers.setAdapter(adapter);
    }

    public void signUp(View view) {
        int userId = dbHelper.addUser(etUsername.getText().toString());
        login(userId);
    }

    public void login(int userId) {
        startActivity(new Intent(this, MainActivity.class).putExtra(USER_ID_EXTRA, userId));
    }

    public void updateUserList() {
        adapter.updateDataSet(dbHelper);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateUserList();
    }
}
