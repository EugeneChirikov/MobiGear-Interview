package ru.mobigear.mobigearinterview.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by eugene on 3/24/15.
 */
public class DataProvider extends ContentProvider {
    public static final String TAG = DataProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DatabaseHelper mOpenHelper;
    private static final int CAFE_ADDRESS = 100;
    private static final int CAFE_SCHEDULE = 101;


    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, DataContract.PATH_CAFE_ADDRESS, CAFE_ADDRESS);
        matcher.addURI(authority, DataContract.PATH_CAFE_SCEDULE, CAFE_SCHEDULE);
        matcher.addURI(authority, DataContract.PATH_ADDITIONAL_SERVICE, ADDITIONAL_SERVICE);
        matcher.addURI(authority, DataContract.PATH_CUISINE, CUISINE);
        matcher.addURI(authority, DataContract.PATH_CAFE, CAFE);
        matcher.addURI(authority, DataContract.PATH_CAFE + "/#", CAFE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = DatabaseHelper.getInstance(getContext());
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

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.v(TAG, uri.toString());
        final int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match) {

            case ORDER_JOIN_CAFE:
                retCursor = defaultQuery(DataContract.Order.TABLE_NAME + " AS a LEFT OUTER JOIN " + DataContract.Cafe.TABLE_NAME + " AS b ON a."
                        + DataContract.Order.COLUMN_CAFE_ID + " = b." + DataContract.Cafe._ID, projection, selection, selectionArgs, sortOrder);
                break;
            case DISHES_FOR_ORDER: {
                String orderId = DataContract.getRowIdFromUri(uri);
                String table = DataContract.OrderDish.TABLE_NAME + " AS a INNER JOIN " + DataContract.OrderCartItem.TABLE_NAME + " AS b ON a."
                        + DataContract.OrderDish._ID + " = b." + DataContract.OrderCartItem.COLUMN_DISH_ID
                        + " INNER JOIN  " + DataContract.Order.TABLE_NAME + " AS c ON b." + DataContract.OrderCartItem.COLUMN_ORDER_ID
                        + " = c." + DataContract.Order._ID;
                selection = "c." + DataContract.Order.COLUMN_ORDER_ID + " = ? ";
                String[] args = new String[]{String.valueOf(orderId)};
                retCursor = defaultQuery(table, projection, selection, args, sortOrder);
                break;
            }
            case CART_JOIN_CAFE: {
                String table = DataContract.Cart.TABLE_NAME + " AS a INNER JOIN " + DataContract.Cafe.TABLE_NAME + " AS b ON a."
                        + DataContract.Cart.COLUMN_CAFE_ID + " = b." + DataContract.Cafe.COLUMN_CAFE_ID; // TODO use CafeWithDetails
                retCursor = defaultQuery(table, projection, selection, selectionArgs, sortOrder);
                break;
            }
            case CART_JOIN_RESERVATION_AND_TABLE: {
                String table = DataContract.Cart.TABLE_NAME + " AS a INNER JOIN " + DataContract.Reservation.TABLE_NAME + " AS b ON a."
                        + DataContract.Cart._ID + " = b." + DataContract.Reservation.COLUMN_CART_ID + " LEFT OUTER JOIN "
                        + DataContract.ReservedTable.TABLE_NAME + " AS c ON b." + DataContract.Reservation._ID + " = c." + DataContract.ReservedTable.COLUMN_RESERVATION_ID;
                retCursor = defaultQuery(table, projection, selection, selectionArgs, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (retCursor != null)
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CUISINE:
                return DataContract.getContentType(DataContract.PATH_CUISINE);
            case ORDER_JOIN_CAFE:
                return DataContract.getContentType(DataContract.PATH_ORDER_JOIN_CAFE);
            case DISH:
                return DataContract.getContentType(DataContract.PATH_ORDER_DISH);
            case ORDER_CART_ITEM:
                return DataContract.getContentType(DataContract.PATH_ORDER_CART_ITEM);
            case ORDER:
                return DataContract.getContentType(DataContract.PATH_ORDER);
            case ORDER_WITH_ID:
                return DataContract.getContentItemType(DataContract.PATH_ORDER);
            case CAFE:
                return DataContract.getContentType(DataContract.PATH_CAFE);
            case CAFE_WITH_ID:
                return DataContract.getContentItemType(DataContract.PATH_CAFE);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private long insertValuesIntoTable(ContentValues values, String tableName) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long _id = db.insert(tableName, null, values);
        if (_id > 0)
            return _id;
        else
            throw new android.database.SQLException("Failed to insert row into " + tableName);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {

            case CAFE_SCHEDULE: {
                long rowId = insertValuesIntoTable(values, DataContract.CafeScedule.TABLE_NAME);
                returnUri = DataContract.buildUri(rowId, DataContract.PATH_CAFE_SCEDULE);
                break;
            }
            case CUISINE: {
                long rowId = insertValuesIntoTable(values, DataContract.Cuisine.TABLE_NAME);
                returnUri = DataContract.buildUri(rowId, DataContract.PATH_CUISINE);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    private int bulkInsert(ContentValues[] values, String tableName) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        int returnCount = 0;
        try {
            for (ContentValues value : values) {
                long _id = db.insert(tableName, null, value);
                if (_id != -1) {
                    returnCount++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return returnCount;
    }



    private int deleteTable(String tableName, String selection, String[] selectionArgs) {
        if (selection == null)
            selection = "";
        return mOpenHelper.getReadableDatabase().delete(
                tableName,
                selection,
                selectionArgs);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) { // TODO notify when delete anything, test on logout
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CART_ITEM: {
                int rowsDeleted = deleteTable(DataContract.CartDish.TABLE_NAME, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;
            }
            case CART: {
                int rowsDeleted = deleteTable(DataContract.Cart.TABLE_NAME, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;
            }
            case CAFE_WITH_DETAILS: {
                return deleteTable(DataContract.CafeWithDetails.TABLE_NAME, selection, selectionArgs);
            }
            case ORDER: {
                return deleteTable(DataContract.Order.TABLE_NAME, selection, selectionArgs);
            }
            case SYNCHRONIZATION: {
                return deleteTable(DataContract.Synchronization.TABLE_NAME, selection, selectionArgs);
                // do not notify
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        int rowsAffected;
        switch (match) {
            case CART_ITEM: {
                rowsAffected = mOpenHelper.getWritableDatabase().update(DataContract.CartDish.TABLE_NAME, values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            }
            case SYNCHRONIZATION: {
                rowsAffected = mOpenHelper.getWritableDatabase().update(DataContract.Synchronization.TABLE_NAME, values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        return rowsAffected;
    }
}