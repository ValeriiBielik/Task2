package com.my.bielik.task2.user;

import android.app.Application;

import com.my.bielik.task2.database.Database;
import com.my.bielik.task2.database.dao.UserDao;
import com.my.bielik.task2.database.entity.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class UserViewModel extends AndroidViewModel {

    private UserDao userDao;
    private LiveData<List<User>> users;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userDao = Database.getInstance(application).userDao();
        users = userDao.getAllUsers();
    }

    long insert(User user) {
        return userDao.insert(user);
    }

    LiveData<List<User>> getAllUsers() {
        return users;
    }
}
