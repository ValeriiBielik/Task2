package com.my.bielik.task2.databases;

import android.provider.BaseColumns;

class PhotoContract {

    private PhotoContract() {
    }

    static final class PhotoEntry implements BaseColumns {
        static final String TABLE_FAVOURITES_NAME = "favourites_table";
        static final String COLUMN_FAVOURITE_URL = "favourite_url";
        static final String COLUMN_FAVOURITE_SEARCH_TEXT = "favourite_search_text";
        static final String COLUMN_USER_ID = "user_id";

        static final String TABLE_RECENT_NAME = "recent_table";
        static final String COLUMN_RECENT_URL = "recent_url";
        static final String COLUMN_TIMESTAMP = "timestamp";

        static final String TABLE_USERS_NAME = "users_table";
        static final String COLUMN_USERNAME = "username";
    }
}
