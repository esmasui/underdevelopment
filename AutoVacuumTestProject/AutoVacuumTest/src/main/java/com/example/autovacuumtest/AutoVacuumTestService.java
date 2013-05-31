
package com.example.autovacuumtest;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AutoVacuumTestService extends IntentService {

    private static final String TAG = "AutoVacuumTest";
    private static final String DATABASE_NAME = "example.db";

    public AutoVacuumTestService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        withoutTransaction();
        withinTransaction();
    }

    private void withoutTransaction() {

        final SQLiteOpenHelper openHelper = getFreshSqLiteOpenHelper();
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        try {
            Log.d(TAG, "auto_vacuum:" + getAutoVacuum(db));
            Log.d(TAG, "without transaction");
            Log.d(TAG, "initial size:" + getDatabaseSize());
            startTest(db, 100);
            Log.d(TAG, "final size:" + getDatabaseSize());
        } finally {
            db.close();
            openHelper.close();
        }
    }

    private void withinTransaction() {

        final SQLiteOpenHelper openHelper = getFreshSqLiteOpenHelper();
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        try {
            Log.d(TAG, "auto_vacuum:" + getAutoVacuum(db));
            Log.d(TAG, "within transaction");
            Log.d(TAG, "initial size:" + getDatabaseSize());
            db.beginTransaction();
            startTest(db, 100);
            db.setTransactionSuccessful();
            db.endTransaction();
            Log.d(TAG, "final size:" + getDatabaseSize());
        } finally {
            db.close();
            openHelper.close();
        }
    }

    private void startTest(SQLiteDatabase db, int count) {

        ContentValues values = new ContentValues();
        for (int i = 0; i < count; ++i) {
            values.clear();
            values.put("name", "foo" + i);
            values.put("email", "foo" + i + "@example.com");
            long insert = db.insert("example", null, values);
            if (insert != i + 1) {
                throw new IllegalStateException("insert failed(" + i + ")");
            }
        }
        Log.d(TAG, "size inserted:" + getDatabaseSize());

        for (int i = 0; i < count; ++i) {
            values.clear();
            values.put("name", "bar" + i);
            values.put("description", "description" + i);
            int update = db.update("example", values, "_id=?", new String[] {
                Integer.toString(i + 1)
            });
            if (update != 1) {
                throw new IllegalStateException("update failed(" + i + ")");
            }
        }
        Log.d(TAG, "size updated:" + getDatabaseSize());

        for (int i = 0; i < count; ++i) {
            int delete = db.delete("example", "_id=?", new String[] {
                Integer.toString(i + 1)
            });
            if (delete != 1) {
                throw new IllegalStateException("delete failed(" + i + ")");
            }
        }
        Log.d(TAG, "size deleted:" + getDatabaseSize());
    }

    private SQLiteOpenHelper getFreshSqLiteOpenHelper() {
        deleteDatabase(DATABASE_NAME);

        final SQLiteOpenHelper openHelper = new AutoVacuumTestSQLiteOpenHelper(this, DATABASE_NAME);
        return openHelper;
    }

    private int getAutoVacuum(SQLiteDatabase db) {
        Cursor autoVacuum = db.rawQuery("pragma auto_vacuum", null);
        try {
            autoVacuum.moveToFirst();
            return autoVacuum.getInt(0);
        } finally {
            autoVacuum.close();
        }
    }

    private long getDatabaseSize() {
        return getDatabasePath(DATABASE_NAME).length();
    }
}
