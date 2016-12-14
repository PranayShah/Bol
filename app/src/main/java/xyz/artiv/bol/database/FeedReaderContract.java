package xyz.artiv.bol.database;


import android.net.Uri;
import android.provider.BaseColumns;

public final class FeedReaderContract {
    private FeedReaderContract() {}

    static final String CONTENT_AUTHORITY = "xyz.artiv.bol.provider";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_THOUGHTS = "thoughts";
    public static class FeedEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_THOUGHTS).build();
        static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_THOUGHTS;
        static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_THOUGHTS;
        static final String TABLE_NAME = "favourites";
        public static final String COLUMN_NAME_DOWNLOAD_URI = "download_uri";
        public static final String COLUMN_NAME_KEY = "key";
        static final String _ID = "_ID";
    }
}
