
package com.uphyca;

import java.util.Map;

import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteQueryBuilder;

public final class Operations {

    static interface Operation {
        void exec(SQLiteQueryBuilder builder);
    }

    static final class AppendWhere implements Operation {
        final CharSequence mInWhere;

        public AppendWhere(CharSequence inWhere) {
            mInWhere = inWhere;
        }

        @Override
        public void exec(SQLiteQueryBuilder builder) {
            builder.appendWhere(mInWhere);
        }
    }

    static final class AppendWhereEscapeString implements Operation {
        final String mInWhere;

        public AppendWhereEscapeString(String inWhere) {
            mInWhere = inWhere;
        }

        @Override
        public void exec(SQLiteQueryBuilder builder) {
            builder.appendWhereEscapeString(mInWhere);
        }
    }

    static final class SetCursorFactory implements Operation {
        final CursorFactory mFactory;

        public SetCursorFactory(CursorFactory factory) {
            mFactory = factory;
        }

        @Override
        public void exec(SQLiteQueryBuilder builder) {
            builder.setCursorFactory(mFactory);
        }
    }

    static final class SetDistinct implements Operation {
        final boolean mDistinct;

        public SetDistinct(boolean distinct) {
            mDistinct = distinct;
        }

        @Override
        public void exec(SQLiteQueryBuilder builder) {
            builder.setDistinct(mDistinct);
        }
    }

    static final class SetTables implements Operation {
        final String mInTables;

        public SetTables(String inTables) {
            mInTables = inTables;
        }

        @Override
        public void exec(SQLiteQueryBuilder builder) {
            builder.setTables(mInTables);
        }
    }

    static final class SetProjectionMap implements Operation {
        final Map<String, String> mColumnMap;

        public SetProjectionMap(Map<String, String> columnMap) {
            mColumnMap = columnMap;
        }

        @Override
        public void exec(SQLiteQueryBuilder builder) {
            builder.setProjectionMap(mColumnMap);
        }
    }

    static final class SetStrict implements Operation {
        final boolean mFlag;

        public SetStrict(boolean flag) {
            mFlag = flag;
        }

        @Override
        public void exec(SQLiteQueryBuilder builder) {
            builder.setStrict(mFlag);
        }
    }
}
