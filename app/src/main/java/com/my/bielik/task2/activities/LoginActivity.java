package com.my.bielik.task2.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.my.bielik.task2.R;
import com.my.bielik.task2.databases.PhotosDBHelper;

public class LoginActivity extends AppCompatActivity {

    public static final String USER_ID_EXTRA = "userId";
    private EditText etLogin;

    private PhotosDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLogin = findViewById(R.id.et_login);

        dbHelper = new PhotosDBHelper(this);

    }

    public void login(View view) {
        int userId = dbHelper.getUserId(etLogin.getText().toString());
        startActivity(new Intent(this, MainActivity.class).putExtra(USER_ID_EXTRA, userId));
    }
}
