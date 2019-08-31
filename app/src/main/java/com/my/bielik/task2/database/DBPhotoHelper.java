package com.my.bielik.task2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.my.bielik.task2.database.object.User;
import com.my.bielik.task2.favourites.Photo;
import com.my.bielik.task2.favourites.RowType;
import com.my.bielik.task2.database.object.PhotoItem;
import com.my.bielik.task2.favourites.Header;

import java.util.List;

import static com.my.bielik.task2.activity.LoginActivity.TAG;
import static com.my.bielik.task2.database.PhotoContract.DB_NAME;
import static com.my.bielik.task2.database.PhotoContract.PhotoEntry.*;

public class DBPhotoHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    public DBPhotoHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " +
                TABLE_FAVOURITES_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FAVOURITE_SEARCH_TEXT + " TEXT, " +
                COLUMN_FAVOURITE_URL + " TEXT NOT NULL, " +
                COLUMN_PHOTO_ID + " TEXT NOT NULL, " +
                COLUMN_USER_ID + " INTEGER NOT NULL " + ");");

        db.execSQL("CREATE TABLE " +
                TABLE_RECENT_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RECENT_URL + " TEXT NOT NULL, " +
                COLUMN_RECENT_SEARCH_TEXT + " TEXT, " +
                COLUMN_PHOTO_ID + " TEXT NOT NULL, " +
                COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_USER_ID + " INTEGER NOt NULL " + ");");

        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_FAVOURITES_NAME);
        db.execSQL("DROP TABLE " + TABLE_RECENT_NAME);

        onCreate(db);
    }

    public void getRecentPhotos(List<PhotoItem> photoItems, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECENT_NAME, null, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}, null, null, COLUMN_TIMESTAMP + " DESC");
        photoItems.clear();
        if (cursor.moveToFirst()) {
            do {
                PhotoItem photoItem = new PhotoItem(cursor.getString(cursor.getColumnIndex(COLUMN_RECENT_SEARCH_TEXT)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_RECENT_URL)), userId,
                        cursor.getString(cursor.getColumnIndex(COLUMN_PHOTO_ID)));
                photoItems.add(photoItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public void addRecent(PhotoItem photoItem) {
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
        cv.put(COLUMN_PHOTO_ID, photoItem.getPhotoId());
        Log.e(TAG, "addRecent: success | id: " + db.insert(TABLE_RECENT_NAME, null, cv));

    }

    public void getFavouritePhotos(List<RowType> dataSet, int userId) {
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
                dataSet.add(new Photo(c.getString(c.getColumnIndex(COLUMN_FAVOURITE_URL)), searchText,
                        c.getString(c.getColumnIndex(COLUMN_PHOTO_ID))));
            } while (c.moveToNext());
            c.close();
        }
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
            cv.put(COLUMN_PHOTO_ID, photoItem.getPhotoId());

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
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_USERNAME + " = ?", new String[]{username}, null, null, null);
        if (cursor.moveToFirst()) {
            id = (int) cursor.getLong(cursor.getColumnIndex(_ID));
            cursor.close();
            Log.e(TAG, "addUser: Failure");
        } else {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_USERNAME, username);
            cursor.close();
            id = (int) db.insert(TABLE_USERS, null, cv);
            Log.e(TAG, "addUser: Success | id: " + id);
        }
        return id;
    }

    public void getUsers(List<User> users) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, null, null, null, null, null);
        users.clear();
        if (cursor.moveToFirst()) {
            do {
                users.add(new User((int) cursor.getLong(cursor.getColumnIndex(_ID)), cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME))));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}
