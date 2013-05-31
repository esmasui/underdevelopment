
package com.uphyca.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class LazyLoadingProvider extends StubProvider {

    private SQLiteOpenHelper mSQLite;

    @Override
    public boolean onCreate() {
        mSQLite = new TestSQLiteOpenHelper(getContext(), "test.db", null, 1);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = mSQLite.getReadableDatabase();
        final String groupBy = null;
        final String having = null;
        final String limit = null;

        SQLiteQueryBuilder queryBuilder = new LazyLoadingSQLiteQueryBuilder();
        queryBuilder.setTables("sample");

        Cursor result = queryBuilder.query(db, projection, selection, selectionArgs, groupBy, having, sortOrder, limit);

        return result;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long id = mSQLite.getWritableDatabase()
                         .insert("sample", null, values);
        return Uri.parse(Long.toString(id));
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return mSQLite.getWritableDatabase()
                      .delete("sample", selection, selectionArgs);
    }
}
