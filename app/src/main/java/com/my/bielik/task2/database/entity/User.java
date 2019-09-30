package com.my.bielik.task2.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table",
        indices = {@Index(value = "userName", unique = true)})
public class User {

    @PrimaryKey(autoGenerate = true)
    private int userID;

    @NonNull
    private String userName;

    public User(@NonNull String userName) {
        this.userName = userName;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    @NonNull
    public String getUserName() {
        return userName;
    }

}
