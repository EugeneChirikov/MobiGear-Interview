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
    public static final String DATABASE_NAME = "mobigearinterview.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_ARTICLE = "CREATE TABLE "
            + DataContract.Article.TABLE_NAME + " ("
            + DataContract.Article._ID + " integer primary key autoincrement, "
            + DataContract.Article.ARTICLE_ID + " integer not null, "
            + DataContract.Article.TITLE + " text not null, "
            + DataContract.Article.BODY + " text not null, "
            + DataContract.Article.IMAGE_URL + " text not null, "
            + " UNIQUE (" + DataContract.Article.ARTICLE_ID + ") ON CONFLICT FAIL );";

    public static final String CREATE_EVENT = "CREATE TABLE "
            + DataContract.Event.TABLE_NAME + " ("
            + DataContract.Event._ID + " integer primary key autoincrement, "
            + DataContract.Event.EVENT_ID + " integer not null, "
            + DataContract.Event.TITLE + " text not null, "
            + DataContract.Event.BODY + " text not null, "
            + DataContract.Event.IMAGE_URL + " text not null, "
            + DataContract.Event.DATE + " text not null, "
            + DataContract.Event.IS_USER_REGISTERED + " integer not null, "
            + " UNIQUE (" + DataContract.Event.EVENT_ID + ") ON CONFLICT FAIL );";

    public static final String CREATE_PROFILE = "CREATE TABLE "
            + DataContract.Profile.TABLE_NAME + " ("
            + DataContract.Profile._ID + " integer primary key autoincrement, "
            + DataContract.Profile.FIO + " text not null, "
            + DataContract.Profile.EMAIL + " text not null, "
            + DataContract.Profile.PHONE + " text not null, "
            + DataContract.Profile.AVATAR_URL + " text not null, "
            + " UNIQUE (" + DataContract.Profile.EMAIL + ") ON CONFLICT FAIL );";

    public static DatabaseHelper getInstance(Context context) {
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
        database.execSQL(CREATE_ARTICLE);
        database.execSQL(CREATE_EVENT);
        database.execSQL(CREATE_PROFILE);
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
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.Article.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.Event.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.Profile.TABLE_NAME);
    }
}
