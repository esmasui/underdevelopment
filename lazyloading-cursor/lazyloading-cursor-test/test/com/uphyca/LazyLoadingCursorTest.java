
package com.uphyca;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.MoreAsserts;
import android.test.ProviderTestCase2;

public class LazyLoadingCursorTest extends ProviderTestCase2<LazyLoadingProvider> {

    private static final String TEST_AUTHORITY = "com.uphyca.lazyloadingcursor";

    public LazyLoadingCursorTest() {
        super(LazyLoadingProvider.class, TEST_AUTHORITY);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        ContentValues hoge = new ContentValues();
        hoge.put("name", "hoge");
        ContentValues piyo = new ContentValues();
        piyo.put("name", "piyo");
        ContentValues fuga = new ContentValues();
        fuga.put("name", "fuga");

        getProvider().bulkInsert(Uri.parse(TEST_AUTHORITY), new ContentValues[] {
                hoge, piyo, fuga
        });
    }

    public void testPreconditions() {
        assertNotNull(getProvider());
    }

    public void testQuery() {
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

        assertEquals(1, result.getCount());
        assertTrue(result.moveToNext());
        assertEquals("hoge", result.getString(result.getColumnIndex("name")));

        result.close();
    }
}
