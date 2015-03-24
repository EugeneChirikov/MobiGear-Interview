package ru.mobigear.mobigearinterview.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by eugene on 3/24/15.
 */
public abstract class AbstractPath {
    private DatabaseHelper mOpenHelper;
    private ContentResolver contentResolver;

    public AbstractPath(DatabaseHelper helper, ContentResolver resolver) {
        this.mOpenHelper = helper;
        this.contentResolver = resolver;
    }

    protected abstract String getPath();
    protected abstract String getTable();

    public String getType() {
        return DataContract.getContentType(getPath());
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor = mOpenHelper.getReadableDatabase().query(
                getTable(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        if (retCursor != null)
            retCursor.setNotificationUri(contentResolver, uri);
        return retCursor;
    }

    public Uri insert(ContentValues values) {
        String tableName = getTable();
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long _id = db.insert(tableName, null, values);
        if (_id > 0) {
            Uri retUri = DataContract.buildUri(_id, getPath());
            contentResolver.notifyChange(retUri, null);
            return retUri;
        }
        else
            throw new android.database.SQLException("Failed to insert row into " + tableName);
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsAffected = mOpenHelper.getWritableDatabase().update(getTable(), values, selection, selectionArgs);
        contentResolver.notifyChange(uri, null);
        return rowsAffected;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (selection == null)
            selection = "";
        int rowsDeleted = mOpenHelper.getReadableDatabase().delete(
                getTable(),
                selection,
                selectionArgs);
        contentResolver.notifyChange(uri, null);
        return rowsDeleted;
    }


}
