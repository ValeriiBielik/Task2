package com.my.bielik.task2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.my.bielik.task2.favourites.RowType;
import com.my.bielik.task2.database.object.PhotoItem;
import com.my.bielik.task2.database.object.User;
import com.my.bielik.task2.favourites.Header;
import com.my.bielik.task2.favourites.Photo;

import java.util.List;

import static com.my.bielik.task2.activity.LoginActivity.TAG;
import static com.my.bielik.task2.database.PhotoContract.PhotoEntry.*;

public class PhotosDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Photos.db";
    private static final int DATABASE_VERSION = 1;

    public PhotosDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_FAVOURITE_PHOTO_LIST_TABLE = "CREATE TABLE " +
                TABLE_FAVOURITES_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FAVOURITE_SEARCH_TEXT + " TEXT NOT NULL, " +
                COLUMN_FAVOURITE_URL + " TEXT NOT NULL, " +
                COLUMN_USER_ID + " INTEGER NOT NULL " + ");";

        String SQL_CREATE_RECENT_PHOTO_LIST_TABLE = "CREATE TABLE " +
                TABLE_RECENT_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RECENT_URL + " TEXT NOT NULL, " +
                COLUMN_RECENT_SEARCH_TEXT + " TEXT NOT NULL, " +
                COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_USER_ID + " INTEGER NOt NULL " + ");";

        String SQL_CREATE_USERS_TABLE = "CREATE TABLE " +
                TABLE_USERS_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT NOT NULL UNIQUE" + ");";

        db.execSQL(SQL_CREATE_FAVOURITE_PHOTO_LIST_TABLE);
        db.execSQL(SQL_CREATE_RECENT_PHOTO_LIST_TABLE);
        db.execSQL(SQL_CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public List<PhotoItem> getRecentPhotos(List<PhotoItem> photoItems, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECENT_NAME, null, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}, null, null, COLUMN_TIMESTAMP + " DESC");
        photoItems.clear();
        if (cursor.moveToFirst()) {
            do {
                PhotoItem photoItem = new PhotoItem(cursor.getString(cursor.getColumnIndex(COLUMN_RECENT_SEARCH_TEXT)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_RECENT_URL)), userId);
                photoItems.add(photoItem);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return photoItems;
    }

    public void addToRecent(PhotoItem photoItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        String id = String.valueOf(photoItem.getUserId());

        db.delete(TABLE_RECENT_NAME, COLUMN_USER_ID + " = ? AND " + COLUMN_RECENT_URL + " = ?",
                new String[]{id, photoItem.getUrl()});

        if (DatabaseUtils.queryNumEntries(db, TABLE_RECENT_NAME, COLUMN_USER_ID + " = ?", new String[]{id}) >= 20) {
            Cursor cursor = db.query(TABLE_RECENT_NAME, null, COLUMN_USER_ID + " = ?",
                    new String[]{id}, null, null, COLUMN_TIMESTAMP);
            if (cursor.moveToFirst()) {
                String s = cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP));

                db.delete(TABLE_RECENT_NAME, COLUMN_TIMESTAMP + " = ? AND " + COLUMN_USER_ID + " = ?", new String[]{s, id});
            }
            cursor.close();
        }

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_RECENT_SEARCH_TEXT, photoItem.getSearchText());
        cv.put(COLUMN_RECENT_URL, photoItem.getUrl());
        cv.put(COLUMN_USER_ID, photoItem.getUserId());
        Log.e(TAG, "addToRecent: success | id: " + db.insert(TABLE_RECENT_NAME, null, cv));

    }

    public List<RowType> getFavouritePhotos(List<RowType> dataSet, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_FAVOURITES_NAME, null, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}, null, null, COLUMN_FAVOURITE_SEARCH_TEXT);

        String searchText = "";
        dataSet.clear();
        if (c.moveToFirst()) {
            do {
                if (!searchText.equals(c.getString(c.getColumnIndex(COLUMN_FAVOURITE_SEARCH_TEXT)))) {
                    searchText = c.getString(c.getColumnIndex(COLUMN_FAVOURITE_SEARCH_TEXT));
                    dataSet.add(new Header(searchText));
                }
                dataSet.add(new Photo(c.getString(c.getColumnIndex(COLUMN_FAVOURITE_URL)), searchText));
            } while (c.moveToNext());
            c.close();
        }
        return dataSet;
    }

    public boolean addFavourite(PhotoItem photoItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_FAVOURITES_NAME, null, COLUMN_FAVOURITE_URL + " = ? AND " + COLUMN_USER_ID + " = ?",
                new String[]{photoItem.getUrl(), String.valueOf(photoItem.getUserId())}, null, null, null);

        if (cursor.moveToFirst()) {
            Log.e(TAG, "addToFavourites: failure");
            cursor.close();
            return false;
        } else {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_FAVOURITE_SEARCH_TEXT, photoItem.getSearchText());
            cv.put(COLUMN_FAVOURITE_URL, photoItem.getUrl());
            cv.put(COLUMN_USER_ID, photoItem.getUserId());

            Log.e(TAG, "addToFavourites: success | id: " +
                    db.insert(TABLE_FAVOURITES_NAME, null, cv));

            cursor.close();
            return true;
        }
    }

    public boolean removeFavourite(PhotoItem photoItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        String[] selectionArgs = new String[]{photoItem.getUrl(), String.valueOf(photoItem.getUserId())};
        Cursor cursor = db.query(TABLE_FAVOURITES_NAME, null, COLUMN_FAVOURITE_URL + " = ? AND " + COLUMN_USER_ID + " = ?",
                selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            Log.e(TAG, "removeFromFavourites: success | removed rows: " +
                    db.delete(TABLE_FAVOURITES_NAME, COLUMN_FAVOURITE_URL + " = ? AND " + COLUMN_USER_ID + " = ?", selectionArgs));
            cursor.close();
            return true;
        } else {
            Log.e(TAG, "removeFromFavourites: failure");
            cursor.close();
            return false;
        }
    }

    public int getFavouritePhotoCount(PhotoItem photoItem) {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_FAVOURITES_NAME,
                COLUMN_USER_ID + " = ? AND " + COLUMN_FAVOURITE_SEARCH_TEXT + " = ?",
                new String[]{String.valueOf(photoItem.getUserId()), photoItem.getSearchText()});
    }

    public int addUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        int id;
        Cursor cursor = db.query(TABLE_USERS_NAME, null, COLUMN_USERNAME + " = ?", new String[]{username}, null, null, null);
        if (cursor.moveToFirst()) {
            id = (int) cursor.getLong(cursor.getColumnIndex(_ID));
            cursor.close();
            Log.e(TAG, "addUser: Failure");
        } else {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_USERNAME, username);
            cursor.close();
            id = (int) db.insert(TABLE_USERS_NAME, null, cv);
            Log.e(TAG, "addUser: Success | id: " + id);
        }
        return id;
    }

    public List<User> getUsers(List<User> users) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS_NAME, null, null, null, null, null, null);
        users.clear();
        if (cursor.moveToFirst()) {
            do {
                users.add(new User((int) cursor.getLong(cursor.getColumnIndex(_ID)), cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return users;
    }
}
