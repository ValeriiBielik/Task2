package com.my.bielik.task2.user;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.my.bielik.task2.R;
import com.my.bielik.task2.database.entity.User;
import com.my.bielik.task2.main.MainActivity;
import com.my.bielik.task2.settings.SettingsActivity;
import com.my.bielik.task2.thread.ProcessResponseThread;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    public static final String USER_ID_EXTRA = "userId";
    public static final String PHOTO_ID_EXTRA = "photoId";
    public static final String URL_EXTRA = "url";
    public static final String SEARCH_TEXT_EXTRA = "search_text";
    public static final String LATITUDE_EXTRA = "latitude";
    public static final String LONGITUDE_EXTRA = "longitude";

    public static final String TAG = "PhotoApp";

    private TextInputEditText inputUserName;
    private RecyclerView rvUsers;
    private ProcessResponseThread processResponseThread = new ProcessResponseThread();

    private UsersAdapter adapter;

    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputUserName = findViewById(R.id.et_username);
        rvUsers = findViewById(R.id.rv_users);

        Toolbar toolbar = findViewById(R.id.settings_bar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
        }

        setUpRecyclerView();

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getAllUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                adapter.setDataSet(users);
            }
        });

        processResponseThread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_settings, menu);
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent settings = new Intent(this, SettingsActivity.class);
        startActivity(settings);
        return true;
    }

    private void setUpRecyclerView() {
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        UsersAdapter.OnItemClickListener listener = new UsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                logIn(adapter.getDataSet().get(position).getUserID());
            }
        };
        adapter = new UsersAdapter(listener);
        rvUsers.setAdapter(adapter);
    }

    public void signUp(View view) {
        processResponseThread.getHandler().post(new Runnable() {
            @Override
            public void run() {
                int userID = (int) userViewModel.insert(new User(inputUserName.getText().toString()));
                if (userID != -1) {
                    logIn(userID);
                } else {
                    Toast.makeText(getApplicationContext(), "Such user already exists", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void logIn(int userID) {
        startActivity(new Intent(this, MainActivity.class).putExtra(USER_ID_EXTRA, userID));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        processResponseThread.quit();
    }
}
