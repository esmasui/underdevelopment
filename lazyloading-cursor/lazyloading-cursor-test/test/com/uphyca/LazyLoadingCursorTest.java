
package com.uphyca;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.MoreAsserts;
import android.test.ProviderTestCase2;
import android.util.Log;

public class LazyLoadingCursorTest extends ProviderTestCase2<LazyLoadingProvider> {

    private static final String TEST_AUTHORITY = "com.uphyca.lazyloadingcursor";

    public LazyLoadingCursorTest() {
        super(LazyLoadingProvider.class, TEST_AUTHORITY);
    }

    public void testPreconditions() {
        assertNotNull(getProvider());
    }

    public void testQuery() {
        //Given
        ContentValues hoge = new ContentValues();
        hoge.put("name", "hoge");
        ContentValues piyo = new ContentValues();
        piyo.put("name", "piyo");
        ContentValues fuga = new ContentValues();
        fuga.put("name", "fuga");

        getProvider().bulkInsert(Uri.parse(TEST_AUTHORITY), new ContentValues[] {
                hoge, piyo, fuga
        });

        //When
        String[] projection = {
                "_id", "name"
        };
        String selection = "name = ?";
        String[] selectionArgs = {
            "hoge"
        };
        String sortOrder = "name";

        Cursor result = getProvider().query(Uri.parse(TEST_AUTHORITY), projection, selection, selectionArgs, sortOrder);

        //Then
        assertNotNull(result);
        MoreAsserts.assertEquals(new String[] {
                "_id", "name"
        }, result.getColumnNames());

        assertEquals(1, result.getCount());
        assertTrue(result.moveToNext());
        assertEquals("hoge", result.getString(result.getColumnIndex("name")));

        result.close();
    }

    public void testThreshold() {
        invokeTestThreshold(128); // DEFAULT_BLOCK_SIZE
        invokeTestThreshold(1);
        invokeTestThreshold(2);
        invokeTestThreshold(10);
    }

    private void invokeTestThreshold(int blockSize) {
        invokeTestThreshold(blockSize, 0);
        invokeTestThreshold(blockSize, 1);
        invokeTestThreshold(blockSize, blockSize);
        invokeTestThreshold(blockSize, blockSize - 1);
        invokeTestThreshold(blockSize, blockSize + 1);
        invokeTestThreshold(blockSize, blockSize * 2);
        invokeTestThreshold(blockSize, blockSize * 2 - 1);
        invokeTestThreshold(blockSize, blockSize * 2 + 1);
        invokeTestThreshold(blockSize, blockSize * 10);
        invokeTestThreshold(blockSize, blockSize * 10 - 1);
        invokeTestThreshold(blockSize, blockSize * 10 + 1);
    }

    private void invokeTestThreshold(int blockSize, int count) {

        List<ContentValues> values = new ArrayList<ContentValues>();
        for (int i = 0; i < count; ++i) {
            ContentValues v = new ContentValues();
            v.put("name", String.format("%08d", i));
            values.add(v);
        }
        Cursor result = null;
        try {

            getProvider().bulkInsert(Uri.parse(TEST_AUTHORITY), values.toArray(new ContentValues[values.size()]));

            //When
            String[] projection = {
                    "_id", "name"
            };
            String selection = null;
            String[] selectionArgs = null;
            String sortOrder = "name";

            result = getProvider().query(Uri.parse(TEST_AUTHORITY), projection, selection, selectionArgs, sortOrder);

            //Then
            assertNotNull(result);
            MoreAsserts.assertEquals(new String[] {
                    "_id", "name"
            }, result.getColumnNames());

            assertEquals(count, result.getCount());
            int fetchCount = 0;
            while (result.moveToNext()) {
                assertEquals(String.format("%08d", fetchCount), result.getString(result.getColumnIndex("name")));
                ++fetchCount;
            }
            assertEquals(count, fetchCount);
        } finally {
            if (result != null) {
                result.close();
            }
            getProvider().delete(Uri.parse(TEST_AUTHORITY), null, null);
        }
    }
}
