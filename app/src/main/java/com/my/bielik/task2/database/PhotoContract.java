package com.my.bielik.task2.database;

import android.provider.BaseColumns;

class PhotoContract {

    private PhotoContract() {
    }

    static final class PhotoEntry implements BaseColumns {
        static final String TABLE_FAVOURITES_NAME = "favourites_table_old";
        static final String COLUMN_FAVOURITE_URL = "favourite_url";
        static final String COLUMN_FAVOURITE_SEARCH_TEXT = "favourite_search_text";

        static final String TABLE_RECENT_NAME = "recent_table_old";
        static final String COLUMN_RECENT_URL = "recent_url";
        static final String COLUMN_RECENT_SEARCH_TEXT = "recent_search_text";

        static final String TABLE_USERS = "users_table";
        static final String COLUMN_USERNAME = "username";

        static final String TABLE_PHOTOS = "photos_table";
        static final String COLUMN_URL = "url";

        static final String TABLE_REQUEST = "request_table";
        static final String COLUMN_SEARCH_TEXT = "search_text";

        static final String TABLE_RECENT = "recent_table";
        static final String TABLE_FAVOURITES = "favourites_table";

        static final String COLUMN_PHOTO_ID = "photo_id";
        static final String COLUMN_SEARCH_TEXT_ID = "search_text_id";
        static final String COLUMN_USER_ID = "user_id";
        static final String COLUMN_TIMESTAMP = "timestamp";


        static final String CREATE_USERS_TABLE = "CREATE TABLE " +
                TABLE_USERS + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT NOT NULL UNIQUE" + ");";

        static final String CREATE_PHOTOS_TABLE = "CREATE TABLE " +
                TABLE_PHOTOS + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_URL + " TEXT NOT NULL UNIQUE" + ");";

        static final String CREATE_SEARCH_TEXT_TABLE = "CREATE TABLE " +
                TABLE_REQUEST + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SEARCH_TEXT + " TEXT NOT NULL UNIQUE" + ");";

        static final String CREATE_RECENT_TABLE = "CREATE TABLE " +
                TABLE_RECENT + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PHOTO_ID + " INTEGER NOT NULL, " +
                COLUMN_SEARCH_TEXT_ID + " INTEGER NOT NULL, " +
                COLUMN_USER_ID + " INTEGER NOT NULL, " +
                COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" + ");";

        static final String CREATE_FAVOURITES_TABLE = "CREATE TABLE " +
                TABLE_FAVOURITES + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PHOTO_ID + " INTEGER NOT NULL, " +
                COLUMN_SEARCH_TEXT_ID + " INTEGER NOT NULL, " +
                COLUMN_USER_ID + " INTEGER NOT NULL" + ");";

    }
}
