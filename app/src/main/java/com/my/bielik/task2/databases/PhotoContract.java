package com.my.bielik.task2.databases;

import android.provider.BaseColumns;

public class PhotoContract {

    private PhotoContract(){}

    public static final class PhotoEntry implements BaseColumns {
        public static final String TABLE_FAVOURITES_NAME = "favourites_table";
        public static final String COLUMN_FAVOURITE_URL = "favourite_url";
        public static final String COLUMN_FAVOURITE_SEARCH_TEXT = "favourite_search_text";

        public static final String TABLE_RECENT_NAME = "recent_table";
        public static final String COLUMN_RECENT_URL = "recent_url";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
