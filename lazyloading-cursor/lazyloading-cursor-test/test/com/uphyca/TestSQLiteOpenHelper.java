package com.uphyca;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class TestSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String CREATE_TABLE_DDL = "CREATE TABLE sample(_id integer primary key autoincrement not null, name VARCHAR);";
    private static final String DROP_TABLE_DDL = "DROP TABLE IF EXISTS sample";

    public TestSQLiteOpenHelper(Context context,
                                String name,
                                CursorFactory factory,
                                int version) {
        super(context,
              name,
              factory,
              version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_DDL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {
        db.execSQL(DROP_TABLE_DDL);
        onCreate(db);
    }
}
