package in.uchneech.bol.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class thoughtsContentProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final String LOG_TAG = thoughtsContentProvider.class.getSimpleName();
    static {
        sUriMatcher.addURI(FeedReaderContract.CONTENT_AUTHORITY, "thoughts/#", 1);
        sUriMatcher.addURI(FeedReaderContract.CONTENT_AUTHORITY, "thoughts", 2);
        sUriMatcher.addURI(FeedReaderContract.CONTENT_AUTHORITY, "thoughts/*", 3);
    }
    feedReaderDbHelper mOpenHelper;
    private SQLiteDatabase db;
    @Override
    public boolean onCreate() {
        mOpenHelper = new feedReaderDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        db = mOpenHelper.getReadableDatabase();
        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
        qBuilder.setTables(FeedReaderContract.FeedEntry.TABLE_NAME);
        if((sUriMatcher.match(uri)) == 3){
            qBuilder.appendWhere( FeedReaderContract.FeedEntry.COLUMN_NAME_KEY + "=" + uri.getLastPathSegment());
        }
        Cursor c = qBuilder.query(db,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case 1:
            case 3:
                return FeedReaderContract.FeedEntry.CONTENT_ITEM_TYPE;
            case 2:
                return FeedReaderContract.FeedEntry.CONTENT_TYPE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        db = mOpenHelper.getWritableDatabase();
        return Uri.withAppendedPath(uri, String.valueOf(db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, contentValues)));
    }

    @Override
    public int delete(@NonNull Uri uri, String s, String[] strings) {
        return db.delete(FeedReaderContract.FeedEntry.TABLE_NAME, s, strings);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
