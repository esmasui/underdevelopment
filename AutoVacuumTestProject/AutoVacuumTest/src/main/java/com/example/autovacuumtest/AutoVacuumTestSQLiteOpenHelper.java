
package com.example.autovacuumtest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AutoVacuumTestSQLiteOpenHelper extends SQLiteOpenHelper {

    public AutoVacuumTestSQLiteOpenHelper(Context context, String databaseName) {
        super(context, databaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("drop table if exists example");
        String description = buildDescription(100);
        sqLiteDatabase.execSQL(String.format("create table if not exists example(_id integer primary key autoincrement, name text, email text unique, description text default '%s')", description));
    }

    private String buildDescription(int count) {
        StringBuilder b = new StringBuilder();
        for(int i = 0; i < count; ++i){
            b.append("The quick brown fox jumps over the lazy dog. ");
        }
        return b.toString();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
