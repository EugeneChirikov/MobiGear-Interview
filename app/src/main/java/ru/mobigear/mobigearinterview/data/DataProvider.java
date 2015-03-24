package ru.mobigear.mobigearinterview.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Created by eugene on 3/24/15.
 */
public class DataProvider extends ContentProvider {
    public static final String TAG = DataProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DatabaseHelper mOpenHelper;
    private ContentResolver contentResolver;
    private static final int ARTICLE = 100;
    private static final int EVENT = 101;
    private static final int PROFILE = 102;


    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, DataContract.PATH_ARTICLE, ARTICLE);
        matcher.addURI(authority, DataContract.PATH_EVENT, EVENT);
        matcher.addURI(authority, DataContract.PATH_PROFILE, PROFILE);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = DatabaseHelper.getInstance(getContext());
        contentResolver = getContext().getContentResolver();
        return true;
    }

    private Cursor defaultQuery(String tableName, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return mOpenHelper.getReadableDatabase().query(
                tableName,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private AbstractPath createPathInstanceFrom(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ARTICLE:
                return new ArticlePath(mOpenHelper, contentResolver);
            case EVENT:
                return new EventPath(mOpenHelper, contentResolver);
            case PROFILE:
                return new ProfilePath(mOpenHelper, contentResolver);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.v(TAG, uri.toString());
        return createPathInstanceFrom(uri).query(uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public String getType(Uri uri) {
        return createPathInstanceFrom(uri).getType();
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return createPathInstanceFrom(uri).insert(values);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return createPathInstanceFrom(uri).delete(uri, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return createPathInstanceFrom(uri).update(uri, values, selection, selectionArgs);
    }
}