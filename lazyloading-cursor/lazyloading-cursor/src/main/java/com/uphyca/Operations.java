package com.uphyca;

import java.util.Map;

import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteQueryBuilder;

public class Operations {

    static interface Operation {
        void exec(SQLiteQueryBuilder builder);
    }

    static final class AppendWhere implements Operation {
        final CharSequence inWhere;

        public AppendWhere(CharSequence inWhere) {
            this.inWhere = inWhere;
        }

        @Override
        public void exec(SQLiteQueryBuilder builder) {
            builder.appendWhere(inWhere);
        }
    }

    static final class AppendWhereEscapeString implements Operation {
        final String inWhere;

        public AppendWhereEscapeString(String inWhere) {
            this.inWhere = inWhere;
        }

        @Override
        public void exec(SQLiteQueryBuilder builder) {
            builder.appendWhereEscapeString(inWhere);
        }
    }

    static final class SetCursorFactory implements Operation {
        final CursorFactory factory;

        public SetCursorFactory(CursorFactory factory) {
            this.factory = factory;
        }

        @Override
        public void exec(SQLiteQueryBuilder builder) {
            builder.setCursorFactory(factory);
        }
    }

    static final class SetDistinct implements Operation {
        final boolean distinct;

        public SetDistinct(boolean distinct) {
            this.distinct = distinct;
        }

        @Override
        public void exec(SQLiteQueryBuilder builder) {
            builder.setDistinct(distinct);
        }
    }

    static final class SetTables implements Operation {
        final String inTables;

        public SetTables(String inTables) {
            this.inTables = inTables;
        }

        @Override
        public void exec(SQLiteQueryBuilder builder) {
            builder.setTables(inTables);
        }
    }

    static final class SetProjectionMap implements Operation {
        final Map<String, String> columnMap;

        public SetProjectionMap(Map<String, String> columnMap) {
            this.columnMap = columnMap;
        }

        @Override
        public void exec(SQLiteQueryBuilder builder) {
            builder.setProjectionMap(columnMap);
        }
    }

    static final class SetStrict implements Operation {
        final boolean flag;

        public SetStrict(boolean flag) {
            this.flag = flag;
        }

        @Override
        public void exec(SQLiteQueryBuilder builder) {
            builder.setStrict(flag);
        }
    }
}
