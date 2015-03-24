package ru.mobigear.mobigearinterview.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by eugene on 3/24/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private static DatabaseHelper mInstance;

    private static final String DATABASE_CREATE_CAFE_WITH_DETAILS = "CREATE TABLE "
            + DataContract.CafeWithDetails.TABLE_NAME + " ("
            + DataContract.CafeWithDetails._ID + " integer primary key autoincrement, "
            + DataContract.CafeWithDetails.COLUMN_CAFE_ID + " integer not null, "
            + DataContract.CafeWithDetails.COLUMN_ALIAS + " text not null, "
            + DataContract.CafeWithDetails.COLUMN_NAME + " text not null, "
            + DataContract.CafeWithDetails.COLUMN_ADDITIONAL_INFO + " text not null, "
            + DataContract.CafeWithDetails.COLUMN_MIN_AMOUNT + " real not null, "
            + DataContract.CafeWithDetails.COLUMN_DELIVERY_PRICE + " real not null, "
            + DataContract.CafeWithDetails.COLUMN_AVG_DELIVERY_TIME + " integer not null, "
            + DataContract.CafeWithDetails.COLUMN_LOGO + " text not null, "
            + DataContract.CafeWithDetails.COLUMN_IS_ORDER_ALLOWED + " integer not null, "
            + DataContract.CafeWithDetails.COLUMN_DESCRIPTION + " text not null, "
            + DataContract.CafeWithDetails.COLUMN_PHONE + " text, "
            + DataContract.CafeWithDetails.COLUMN_EMAIL + " text, "
            + DataContract.CafeWithDetails.COLUMN_WEBSITE + " text, "
            + DataContract.CafeWithDetails.COLUMN_MENU_ID + " integer not null, "
            + DataContract.CafeWithDetails.COLUMN_ADDRESS_ID + " integer not null, "
            + " UNIQUE (" + DataContract.CafeWithDetails.COLUMN_CAFE_ID + ") ON CONFLICT FAIL, "
            + " FOREIGN KEY ( " + DataContract.CafeWithDetails.COLUMN_ADDRESS_ID + " ) REFERENCES "
            + DataContract.CafeAddress.TABLE_NAME + "(" + DataContract.CafeAddress._ID + ") ON DELETE CASCADE " + ");";



    public static final String DATABASE_NAME = "mobigearinterview.db";
    private static final int DATABASE_VERSION = 1;

    public static DatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    private DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL(DATABASE_CREATE_CAFE_DELIVERIES_LINK);
        database.execSQL(DATABASE_CREATE_ADDITIONAL_SERVICE);

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        clearDatabase(db);
        onCreate(db);
    }

    public synchronized void clearDatabase() {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        db.beginTransaction();
        clearDatabase(db);
        onCreate(db);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    private void clearDatabase(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.CafeWithDetails.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.CafeAddress.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.Cafe.TABLE_NAME);
    }
}
