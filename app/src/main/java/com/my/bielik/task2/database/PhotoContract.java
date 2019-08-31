package com.my.bielik.task2.database;

import android.provider.BaseColumns;

final class PhotoContract {

    static final String DB_NAME = "Photos.db";

    static final class PhotoEntry implements BaseColumns {
        static final String TABLE_FAVOURITES_NAME = "favourites_table_old";
        static final String COLUMN_FAVOURITE_URL = "favourite_url";
        static final String COLUMN_FAVOURITE_SEARCH_TEXT = "favourite_search_text";

        static final String TABLE_RECENT_NAME = "recent_table_old";
        static final String COLUMN_RECENT_URL = "recent_url";
        static final String COLUMN_RECENT_SEARCH_TEXT = "recent_search_text";

        static final String TABLE_USERS = "users_table";
        static final String COLUMN_USERNAME = "username";

        static final String COLUMN_USER_ID = "user_id";
        static final String COLUMN_TIMESTAMP = "timestamp";

        static final String COLUMN_PHOTO_ID = "photo_id";

        static final String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_USERS + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT NOT NULL UNIQUE" + ");";
    }
}
