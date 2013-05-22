
package com.uphyca;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.suitebuilder.annotation.Suppress;
import android.util.Log;

public abstract class Benchmark<T extends ContentProvider> extends ProviderTestCase2<T> {

    private static final String TEST_AUTHORITY = "com.uphyca.lazyloadingcursor";
    private static final int RECORD_COUNT = 30000;
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

        for (int i = 0; i < RECORD_COUNT; ++i) {

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

    public void testTinyRecords() {
        invokeTest("_id <= ?", //
                   new String[] {
                       "1000",
                   });
    }

    public void testSmallRecords() {
        invokeTest("name = ?", //
                   new String[] {
                       "hoge",
                   });
    }

    public void testMediumRecords() {
        invokeTest("name in (?, ?)", //
                   new String[] {
                           "hoge", "piyo",
                   });
    }

    public void testLargeRecords() {
        invokeTest("name in (?, ?, ?)", //
                   new String[] {
                           "hoge", "piyo", "fuga",
                   });
    }

    void invokeTest(String selection, String[] selectionArgs) {
        long current = System.currentTimeMillis();

        String[] projection = {
                "_id", "name"
        };
        String sortOrder = "name";

        Cursor result = getProvider().query(Uri.parse(TEST_AUTHORITY), projection, selection, selectionArgs, sortOrder);

        assertNotNull(result);

        assertTrue(result.moveToNext());

        Log.i("Benchmark", toString() + ":fetchFirst==" + (System.currentTimeMillis() - current) + "(ms)");

        while (FETCH_ALL && result.moveToNext()) {
        }

        result.close();

        Log.i("Benchmark", toString() + ":fetchAll=" + (System.currentTimeMillis() - current) + "(ms)");
    }
}
