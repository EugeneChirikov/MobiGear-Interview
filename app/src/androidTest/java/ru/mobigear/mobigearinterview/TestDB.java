package ru.mobigear.mobigearinterview;

import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.InstrumentationTestCase;

import ru.mobigear.mobigearinterview.data.DatabaseHelper;
import ru.mobigear.mobigearinterview.demo.TestUtils;


/**
 * Created by echirikov on 18.09.14.
 */
public class TestDB extends InstrumentationTestCase {
    public static final String TAG = TestDB.class.getSimpleName();
    private ContentResolver contentResolver;
    private Context targetContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        contentResolver = getInstrumentation().getContext().getContentResolver();
        targetContext = getInstrumentation().getTargetContext();
    }

    public void test1CreateDb() throws Throwable {
        boolean doesDatabaseExist = TestUtils.doesDatabaseExist(targetContext, DatabaseHelper.DATABASE_NAME);
        if (doesDatabaseExist) {
            boolean isDatabaseDeleted = targetContext.deleteDatabase(DatabaseHelper.DATABASE_NAME);
            assertEquals(true, isDatabaseDeleted);
        }
        SQLiteDatabase db = DatabaseHelper.getInstance(getInstrumentation().getContext()).getWritableDatabase();
        assertNotNull(db);
        assertEquals(true, db.isOpen());
        db.close();
    }
}
