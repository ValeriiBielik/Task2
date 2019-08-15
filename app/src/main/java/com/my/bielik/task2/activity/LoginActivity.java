package com.my.bielik.task2.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.my.bielik.task2.R;
import com.my.bielik.task2.UsersAdapter;
import com.my.bielik.task2.database.PhotosDBHelper;
import com.my.bielik.task2.database.object.User;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    public static final String USER_ID_EXTRA = "userId";
    public static final String URL_EXTRA = "url";
    public static final String SEARCH_TEXT_EXTRA = "search_text";
    public static final String TAG = "PhotoApp";

    private EditText etLogin;
    private RecyclerView rvUsers;

    private PhotosDBHelper dbHelper;
    private UsersAdapter adapter;

    private List<User> usernameList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLogin = findViewById(R.id.et_login);
        rvUsers = findViewById(R.id.rv_users);

        dbHelper = new PhotosDBHelper(this);

        setUpRecyclerView();

        updateUserList();

    }

    public void setUpRecyclerView() {
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UsersAdapter(usernameList);
        rvUsers.setAdapter(adapter);

        adapter.setOnItemClickListener(new UsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                login(usernameList.get(position).getId());
            }
        });
    }

    public void signUp(View view) {
        int userId = dbHelper.addUser(etLogin.getText().toString());
        login(userId);
    }

    public void login(int userId) {
        startActivity(new Intent(this, MainActivity.class).putExtra(USER_ID_EXTRA, userId));
    }

    public void updateUserList() {
        usernameList = dbHelper.getUsers(usernameList);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateUserList();
    }
}
