package ru.mobigear.mobigearinterview.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by eugene on 3/24/15.
 */
public class AbstractPath {
    private DatabaseHelper mOpenHelper;
    private ContentResolver contentResolver;

    public AbstractPath(DatabaseHelper helper, ContentResolver resolver) {
        this.mOpenHelper = helper;
        this.contentResolver = resolver;
    }
    public String getType(Uri uri);
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder);
    public Uri insert(Uri uri, ContentValues values);
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs);
    public int delete(Uri uri, String selection, String[] selectionArgs);

    protected long insertValuesIntoTable(ContentValues values, String tableName) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long _id = db.insert(tableName, null, values);
        if (_id > 0)
            return _id;
        else
            throw new android.database.SQLException("Failed to insert row into " + tableName);
    }

    protected Cursor defaultQuery(String tableName, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return mOpenHelper.getReadableDatabase().query(
                tableName,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }
}
