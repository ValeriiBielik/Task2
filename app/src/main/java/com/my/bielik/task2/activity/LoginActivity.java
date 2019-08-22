package com.my.bielik.task2.activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.my.bielik.task2.R;
import com.my.bielik.task2.UsersAdapter;
import com.my.bielik.task2.database.PhotosDBHelper;

public class LoginActivity extends AppCompatActivity {

    public static final String USER_ID_EXTRA = "userId";
    public static final String URL_EXTRA = "url";
    public static final String SEARCH_TEXT_EXTRA = "search_text";
    public static final String LATITUDE_EXTRA = "latitude";
    public static final String LONGITUDE_EXTRA = "longitude";

    public static final String TAG = "PhotoApp";

    private EditText etLogin;
    private RecyclerView rvUsers;

    private PhotosDBHelper dbHelper;
    private UsersAdapter adapter;

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
        int userId = dbHelper.addUser(etLogin.getText().toString());
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
