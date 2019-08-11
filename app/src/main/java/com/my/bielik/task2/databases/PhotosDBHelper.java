package com.my.bielik.task2.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.my.bielik.task2.database_objects.DatabasePhotoItem;

import java.util.ArrayList;

import static com.my.bielik.task2.databases.PhotoContract.PhotoEntry.*;

public class PhotosDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "PhotosDBHelper";
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
                COLUMN_FAVOURITE_URL + " TEXT NOT NULL UNIQUE" + ");";

        String SQL_CREATE_RECENT_PHOTO_LIST_TABLE = "CREATE TABLE " +
                TABLE_RECENT_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RECENT_URL + " TEXT NOT NULL UNIQUE, " +
                COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP " + ");";

        db.execSQL(SQL_CREATE_FAVOURITE_PHOTO_LIST_TABLE);
        db.execSQL(SQL_CREATE_RECENT_PHOTO_LIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public DatabasePhotoItem getRecentPhotos(Context context) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECENT_NAME, null, null, null, null, null, COLUMN_TIMESTAMP + " DESC");

        DatabasePhotoItem databasePhotoItem = new DatabasePhotoItem(context, null);
        if (cursor.moveToFirst()) {
            do {
                databasePhotoItem.updateUrlList(cursor.getString(cursor.getColumnIndex(COLUMN_RECENT_URL)));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return databasePhotoItem;
    }

    public ArrayList<DatabasePhotoItem> getFavouritePhotos(Context context, ArrayList<DatabasePhotoItem> databasePhotoItems) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = new String[]{COLUMN_FAVOURITE_SEARCH_TEXT, COLUMN_FAVOURITE_URL};
        Cursor c = db.query(TABLE_FAVOURITES_NAME, columns, null, null, null, null, COLUMN_FAVOURITE_SEARCH_TEXT);

        databasePhotoItems.clear();
        DatabasePhotoItem databasePhotoItem = null;
        String searchText = "";

        if (c.moveToFirst()) {
            do {
                if (!searchText.equals(c.getString(c.getColumnIndex(COLUMN_FAVOURITE_SEARCH_TEXT)))) {
                    searchText = c.getString(c.getColumnIndex(COLUMN_FAVOURITE_SEARCH_TEXT));
                    if (databasePhotoItem != null) {
                        databasePhotoItems.add(databasePhotoItem);
                    }
                    databasePhotoItem = new DatabasePhotoItem(context, searchText);
                }
                databasePhotoItem.updateUrlList(c.getString(c.getColumnIndex(COLUMN_FAVOURITE_URL)));
            } while (c.moveToNext());
            c.close();
            databasePhotoItems.add(databasePhotoItem);
        }
        return databasePhotoItems;
    }

    public boolean addFavourite(String searchText, String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_FAVOURITES_NAME, null, COLUMN_FAVOURITE_URL + " = ?", new String[]{url}, null, null, null);

        if (cursor.moveToFirst()) {
            Log.e(TAG, "addToFavourites: failure");
            cursor.close();
            return false;
        } else {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_FAVOURITE_SEARCH_TEXT, searchText);
            cv.put(COLUMN_FAVOURITE_URL, url);

            Log.e(TAG, "addToFavourites: success | id: " +
                    db.insert(TABLE_FAVOURITES_NAME, null, cv));

            cursor.close();
            return true;
        }
    }

    public boolean removeFavourite(String url) {
        SQLiteDatabase db = this.getWritableDatabase();

        String[] selectionArgs = new String[]{url};
        Cursor cursor = db.query(TABLE_FAVOURITES_NAME, null, COLUMN_FAVOURITE_URL + " = ?",
                selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            Log.e(TAG, "removeFromFavourites: success | removed rows: " +
                    db.delete(TABLE_FAVOURITES_NAME, COLUMN_FAVOURITE_URL + " = ?", selectionArgs));
            cursor.close();
            return true;
        } else {
            Log.e(TAG, "removeFromFavourites: failure");
            cursor.close();
            return false;
        }
    }

    public void addRecent(String url) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_RECENT_NAME, null, COLUMN_RECENT_URL + " = ?", new String[]{url},
                null, null, null);
        if (cursor.moveToFirst()) {
            Log.e(TAG, "addToRecent: failure");
            cursor.close();
        } else {
            if (DatabaseUtils.queryNumEntries(db, TABLE_RECENT_NAME) >= 20) {
                cursor = db.query(TABLE_RECENT_NAME, null, null, null, null, null, COLUMN_TIMESTAMP);
                if (cursor.moveToFirst()) {
                    String s = cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP));

                    int n = db.delete(TABLE_RECENT_NAME, COLUMN_TIMESTAMP + " = ?", new String[]{s});
                    Log.e(TAG, "deletedFromRecent: success | deleted rows: " + n + " | date: " + s);
                }
                cursor.close();
            }
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_RECENT_URL, url);
            Log.e(TAG, "addToRecent: success | id: " + db.insert(TABLE_RECENT_NAME, null, cv));
        }
    }
}
