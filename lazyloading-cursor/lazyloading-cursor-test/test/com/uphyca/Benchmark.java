
package com.uphyca;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.MoreAsserts;
import android.test.ProviderTestCase2;
import android.test.suitebuilder.annotation.Suppress;
import android.util.Log;

public abstract class Benchmark<T extends ContentProvider> extends ProviderTestCase2<T> {

    private static final String TEST_AUTHORITY = "com.uphyca.lazyloadingcursor";
    private static final int EXPECTED_COUNT = 30000;
    private static final boolean FETCH_ALL = true;

    public Benchmark(Class<T> providerClass) {
        super(providerClass, TEST_AUTHORITY);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        System.gc();

        TestSQLiteOpenHelper sqLiteOpenHelper = new TestSQLiteOpenHelper(getMockContext(), "test.db", null, 1);
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        db.beginTransaction();

        for (int i = 0; i < EXPECTED_COUNT; ++i) {

            ContentValues hoge = new ContentValues();
            hoge.put("name", "hoge");
            ContentValues piyo = new ContentValues();
            piyo.put("name", "piyo");
            ContentValues fuga = new ContentValues();
            fuga.put("name", "fuga");
            db.insert("sample", null, hoge);
            db.insert("sample", null, piyo);
            db.insert("sample", null, fuga);

        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        System.gc();
    }

    @Override
    protected void tearDown() throws Exception {
        getMockContext().getDatabasePath("test.db")
                        .delete();
        super.tearDown();
    }

    @Override
    @Suppress
    public void testAndroidTestCaseSetupProperly() {
    }

    public void testSmallRecords() {
        long current = System.currentTimeMillis();

        String[] projection = {
                "_id", "name"
        };
        String selection = "name = ?";
        String[] selectionArgs = {
            "hoge"
        };
        String sortOrder = "name";

        Cursor result = getProvider().query(Uri.parse(TEST_AUTHORITY), projection, selection, selectionArgs, sortOrder);

        assertNotNull(result);
        MoreAsserts.assertEquals(new String[] {
                "_id", "name"
        }, result.getColumnNames());

        assertEquals(EXPECTED_COUNT, result.getCount());
        assertTrue(result.moveToNext());
        assertEquals("hoge", result.getString(result.getColumnIndex("name")));

        while (FETCH_ALL && result.moveToNext()) {
        }

        result.close();

        long end = System.currentTimeMillis();

        Log.i("Benchmark", toString() + "=" + (end - current) + "(ms)");

    }

    public void testMediumRecords() {
        long current = System.currentTimeMillis();

        String[] projection = {
                "_id", "name"
        };
        String selection = "name in (?, ?)";
        String[] selectionArgs = {
                "hoge", "piyo"
        };
        String sortOrder = "name";

        Cursor result = getProvider().query(Uri.parse(TEST_AUTHORITY), projection, selection, selectionArgs, sortOrder);

        assertNotNull(result);
        MoreAsserts.assertEquals(new String[] {
                "_id", "name"
        }, result.getColumnNames());

        assertEquals(EXPECTED_COUNT * 2, result.getCount());
        assertTrue(result.moveToNext());
        assertEquals("hoge", result.getString(result.getColumnIndex("name")));

        while (FETCH_ALL && result.moveToNext()) {
        }

        result.close();

        long end = System.currentTimeMillis();

        Log.i("Benchmark", toString() + "=" + (end - current) + "(ms)");
    }

    public void testLargeRecords() {
        long current = System.currentTimeMillis();

        String[] projection = {
                "_id", "name"
        };
        String selection = "name in (?, ?, ?)";
        String[] selectionArgs = {
                "hoge", "piyo", "fuga"
        };
        String sortOrder = "name";

        Cursor result = getProvider().query(Uri.parse(TEST_AUTHORITY), projection, selection, selectionArgs, sortOrder);

        assertNotNull(result);
        MoreAsserts.assertEquals(new String[] {
                "_id", "name"
        }, result.getColumnNames());

        assertEquals(EXPECTED_COUNT * 3, result.getCount());
        assertTrue(result.moveToNext());
        assertEquals("fuga", result.getString(result.getColumnIndex("name")));

        while (FETCH_ALL && result.moveToNext()) {
        }

        result.close();

        long end = System.currentTimeMillis();

        Log.i("Benchmark", toString() + "=" + (end - current) + "(ms)");
    }

}
