package com.my.bielik.task2.database;

import android.content.Context;

import com.my.bielik.task2.database.dao.FavouritePhotoDao;
import com.my.bielik.task2.database.dao.PhotoDao;
import com.my.bielik.task2.database.dao.RecentPhotoDao;
import com.my.bielik.task2.database.dao.UserDao;
import com.my.bielik.task2.database.entity.FavouritePhoto;
import com.my.bielik.task2.database.entity.Photo;
import com.my.bielik.task2.database.entity.RecentPhoto;
import com.my.bielik.task2.database.entity.User;

import androidx.room.Room;
import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {User.class, Photo.class, FavouritePhoto.class, RecentPhoto.class}, version = 2, exportSchema = false)
public abstract class Database extends RoomDatabase {

    private static Database instance;

    public abstract UserDao userDao();

    public abstract PhotoDao photoDao();

    public abstract FavouritePhotoDao favouritePhotoDao();

    public abstract RecentPhotoDao recentPhotoDao();


    public static synchronized Database getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    Database.class, "main_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
