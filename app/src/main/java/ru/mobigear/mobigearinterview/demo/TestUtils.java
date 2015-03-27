package ru.mobigear.mobigearinterview.demo;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import junit.framework.Assert;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;

import ru.mobigear.mobigearinterview.data.DataContract;

/**
 * Created by eugene on 3/26/15.
 */
public class TestUtils {
    public static void validateEmpty(ContentResolver r, String uriPath) {
        Cursor cursor = r.query(DataContract.getContentUri(uriPath), null, null, null, null);
        Assert.assertTrue(cursor == null || !cursor.moveToFirst());
        closeCursor(cursor);
    }

    private static void closeCursor(Cursor cursor) {
        if (cursor != null && !cursor.isClosed())
            cursor.close();
    }

    public static void validateInserted(ContentResolver r, String uriPath, String selection, ContentValues[] expectedValues) {
        Cursor cursor = r.query(DataContract.getContentUri(uriPath), null, selection, null, null);
        for (int i = 0; i < expectedValues.length; ++i)
            validateCursor(i, cursor, expectedValues[i]);
        closeCursor(cursor);
    }

    public static void validateInserted(ContentResolver r, String uriPath, ContentValues[] expectedValues) {
        validateInserted(r, uriPath, null, expectedValues);
    }

    public static void validateCursor(int position, Cursor valueCursor, ContentValues expectedValues) {
        Assert.assertTrue(valueCursor.moveToPosition(position));
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            Assert.assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            String value = valueCursor.getString(idx);
            if (value == null)
                value = "null";
            Assert.assertEquals(expectedValue, value);
        }
    }

    public static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    public static void initContentValues(ContentValues[] values) {
        for (int i = 0; i < values.length; ++i)
            values[i] = new ContentValues();
    }

    public static String readString(InputStream inputStream){
        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder total = new StringBuilder();
        String line = "";
        try {
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return total.toString();
    }
}
