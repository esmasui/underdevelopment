/*
 * Copyright (C) 2013 uPhyca Inc. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uphyca;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.database.AbstractCursor;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

class LazyLoadingCursor extends AbstractCursor {

    private final class LazyLoadingDataSetObserver extends DataSetObserver {
        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mCount = -1;
            mCursor = null;
        }
    }

    private final DataSetObserver mDataSetObserver = new LazyLoadingDataSetObserver();

    private Cursor mCounter;
    private final int mBlockSize;

    private final Context mContext;
    private final Uri mContentUri;
    private CursorProxy[] mCursors;
    private CursorProxy mCursor;
    private int mCount;

    private final String[] mColumns;
    private final String mSelection;
    private final String[] mSelectionArgs;
    private final String mGroupBy;
    private final String mHaving;
    private final String mOrderBy;

    private String[] mColumnNames;

    private final List<Operations.Operation> mOperations;

    private final SQLiteDatabase mCounterDatabase;
    private final SQLiteDatabase mDatabase;
    private final SQLiteQueryBuilder mQueryBuilder;

    private static final ExecutorService sExec = Executors.newCachedThreadPool();

    public LazyLoadingCursor(Context context, Uri uri, SQLiteDatabase db, List<Operations.Operation> op, final String[] projectionIn, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder, final String limit, int blockSize) {
        mContext = context;
        mContentUri = uri;
        mDatabase = db;
        mCounterDatabase = SQLiteDatabase.openDatabase(db.getPath(), null, SQLiteDatabase.OPEN_READONLY);
        mOperations = op;
        mBlockSize = blockSize;
        mColumns = projectionIn;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mGroupBy = groupBy;
        mHaving = having;
        mOrderBy = sortOrder;
        mQueryBuilder = execOperations(new SQLiteQueryBuilder());

        final CountDownLatch sync = new CountDownLatch(3);
        final CursorProxy[] cursorHoler = new CursorProxy[1];

        sExec.execute(new Runnable() {
            @Override
            public void run() {
                mCounter = initCounter(limit);
                sync.countDown();
            };
        });
        sExec.execute(new Runnable() {
            @Override
            public void run() {
                mColumnNames = initColumnNames(projectionIn);
                sync.countDown();
            };
        });
        sExec.execute(new Runnable() {
            @Override
            public void run() {
                cursorHoler[0] = initFirstCursor();
                sync.countDown();
            };
        });
        try {
            sync.await();
        } catch (InterruptedException e) {
            close();
            return;
        }
        mCount = mCounter.getInt(0);
        mCursors = new CursorProxy[calcBlockCount(mCount, blockSize)];
        if (mCursors.length > 0) {
            mCursors[0] = cursorHoler[0];
        }
    }

    private final Cursor initCounter(String limit) {
        String rawSql = mQueryBuilder.buildQuery(mColumns, mSelection, null, mGroupBy, mHaving, mOrderBy, limit);
        String countSql = String.format("SELECT COUNT(" + (mColumns != null ? mColumns[0] : "'X'") + ") COUNT FROM(%s) LIMIT 1", rawSql);
        Cursor returnThis = mCounterDatabase.rawQuery(countSql, mSelectionArgs);
        returnThis.registerDataSetObserver(mDataSetObserver);
        returnThis.setNotificationUri(mContext.getContentResolver(), mContentUri);
        returnThis.moveToFirst();
        return returnThis;
    }

    private final String[] initColumnNames(String[] projectionIn) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(mDatabase.getPath(), null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = null;
        try {
            cursor = mQueryBuilder.query(db, projectionIn, mSelection, mSelectionArgs, mGroupBy, mHaving, null, "0,0");
            return cursor.getColumnNames();
        } finally {
            cursor.close();
            db.close();
        }
    }

    private final CursorProxy initFirstCursor() {
        final int actualBlockSize = calcActualBlockSize(mBlockSize, 0);
        CursorProxy returnThis = new CursorProxy(null, 0, actualBlockSize);
        returnThis.moveToFirst();
        return returnThis;
    }

    private SQLiteQueryBuilder execOperations(SQLiteQueryBuilder builder) {
        for (Operations.Operation each : mOperations) {
            each.exec(builder);
        }
        return builder;
    }

    private static final int calcActualBlockSize(int blockSize, int position) {
        return (int) (blockSize * Math.pow(2, position));
    }

    private static final int calcBlockCount(int rawCount, int blockSize) {
        if (rawCount == 0) {
            return 0;
        }

        for (int i = 0, actualCount = 0; i <= rawCount; ++i) {
            actualCount += calcActualBlockSize(blockSize, i);
            if (actualCount >= rawCount) {
                return (i + 1);
            }
        }
        return 0;
    }

    private static final int calcOffsetCount(int size, int blockSize, int position) {
        if (size == 0) {
            return -1;
        }

        int returnThis = 0;
        for (int i = 0; i < position; ++i) {
            returnThis += calcActualBlockSize(blockSize, i);
        }

        return returnThis;
    }

    private void reallocate(Context context, Uri contentUri, SQLiteDatabase db) {
        if (mCounter.isBeforeFirst()) {
            if (!mCounter.moveToFirst()) {
                return;
            }
        }
        mCount = mCounter.getInt(0);
        final int blockCount = calcBlockCount(mCount, mBlockSize);

        CursorProxy[] oldCursors = mCursors;
        if (oldCursors != null) {
            for (CursorProxy each : oldCursors) {
                if (each != null) {
                    each.close();
                }
            }
        }

        mCursors = new CursorProxy[blockCount];
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public boolean onMove(int oldPosition, int newPosition) {
        final CursorProxy c = mCursor;

        if (c != null && c.hasPosition(newPosition)) {
            return c.moveToAbsolutePosition(newPosition);
        }

        final CursorProxy[] cursors = mCursors;
        for (int i = 0, size = cursors.length; i < size; ++i) {
            CursorProxy each = cursors[i];
            if (each == null) {
                each = new CursorProxy(null, calcOffsetCount(mCount, mBlockSize, i), calcActualBlockSize(mBlockSize, i));
                each.setNotificationUri(mContext.getContentResolver(), mContentUri);
                cursors[i] = each;
            }
            if (each.hasPosition(newPosition)) {
                mCursor = each;
                return each.moveToAbsolutePosition(newPosition);
            }
        }

        return false;
    }

    // /**
    // * @hide
    // * @deprecated
    // */
    // @Override
    // public boolean deleteRow() {
    // return cursor.deleteRow();
    // }
    //
    // /**
    // * @hide
    // * @deprecated
    // */
    // @Override
    // public boolean commitUpdates() {
    // for (int i = 0, length = cursors.size(); i < length; i++) {
    // Cursor c = cursors.get(i);
    // if (c != null) {
    // c.commitUpdates();
    // }
    // }
    // onChange(true);
    // return true;
    // }

    @Override
    public String getString(int column) {
        return mCursor.getString(column);
    }

    @Override
    public short getShort(int column) {
        return mCursor.getShort(column);
    }

    @Override
    public int getInt(int column) {
        return mCursor.getInt(column);
    }

    @Override
    public long getLong(int column) {
        return mCursor.getLong(column);
    }

    @Override
    public float getFloat(int column) {
        return mCursor.getFloat(column);
    }

    @Override
    public double getDouble(int column) {
        return mCursor.getDouble(column);
    }

    @Override
    public boolean isNull(int column) {
        return mCursor.isNull(column);
    }

    @Override
    public byte[] getBlob(int column) {
        return mCursor.getBlob(column);
    }

    // @Override
    // public String[] getColumnNames() {
    // if (cursor != null) {
    // return cursor.getColumnNames();
    // } else {
    // return new String[0];
    // }
    // }

    @Override
    public String[] getColumnNames() {
        return mColumnNames;
    }

    @Override
    public void deactivate() {
        for (Cursor each : mCursors) {
            if (each != null) {
                each.deactivate();
            }
        }
        mCounter.deactivate();
        super.deactivate();
    }

    @Override
    public void close() {
        if (mCursors != null) {
            for (Cursor each : mCursors) {
                if (each != null) {
                    each.close();
                }
            }
            mCursors = null;
        }
        if (mCounter != null) {
            mCounter.close();
            mCounter = null;
        }
        if (mCounterDatabase != null) {
            mCounterDatabase.close();
        }
        if (mCursor != null) {
            mCursor = null;
            mCursor = null;
        }
        super.close();
    }

    @Override
    public void registerContentObserver(ContentObserver observer) {
        if (mCursors != null) {
            for (CursorProxy each : mCursors) {
                if (each != null) {
                    each.registerContentObserver(observer);
                }
            }
        }
        mCounter.registerContentObserver(observer);
    }

    @Override
    public void unregisterContentObserver(ContentObserver observer) {
        if (mCursors != null) {
            for (CursorProxy each : mCursors) {
                if (each != null) {
                    each.unregisterContentObserver(observer);
                }
            }
        }
        mCounter.unregisterContentObserver(observer);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        if (mCursors != null) {
            for (CursorProxy each : mCursors) {
                if (each != null) {
                    each.registerDataSetObserver(observer);
                }
            }
        }
        mCounter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (mCursors != null) {
            for (CursorProxy each : mCursors) {
                if (each != null) {
                    each.unregisterDataSetObserver(observer);
                }
            }
        }
        mCounter.unregisterDataSetObserver(observer);
    }

    @Override
    public boolean requery() {
        if (mCounter.requery() == false) {
            return false;
        }

        reallocate(mContext, mContentUri, mDatabase);
        return true;
    }

    private final class CursorProxy extends AbstractCursor {

        private Set<DataSetObserver> mDataSetObservers;
        private Set<ContentObserver> mContentObservers;

        private Cursor mUnderlying;
        private final int mOffsetCount;
        private final int mActualBlockSize;

        public CursorProxy(Cursor cursor, int offsetCount, int actualBlockSize) {
            mUnderlying = cursor;
            mOffsetCount = offsetCount;
            mActualBlockSize = actualBlockSize;
        }

        final void swapCursor(Cursor cursor) {
            if (mContentObservers != null) {
                for (ContentObserver observer : mContentObservers)
                    cursor.registerContentObserver(observer);
            }
            if (mDataSetObservers != null) {
                for (DataSetObserver observer : mDataSetObservers)
                    cursor.registerDataSetObserver(observer);
            }
            mUnderlying = cursor;
        }

        final boolean hasPosition(int absolutePosition) {
            return (mOffsetCount <= absolutePosition) && (mOffsetCount + mActualBlockSize > absolutePosition);
        }

        final boolean moveToAbsolutePosition(int absolutePosition) {
            return moveToPosition(absolutePosition - mOffsetCount);
        }

        @Override
        public int getCount() {
            return mActualBlockSize;
        }

        @Override
        public boolean onMove(int oldPosition, int newPosition) {
            if (mUnderlying == null) {
                final String limit = mOffsetCount + "," + mActualBlockSize;
                SQLiteCursor c = (SQLiteCursor) mQueryBuilder.query(mDatabase, mColumns, mSelection, mSelectionArgs, mGroupBy, mHaving, mOrderBy, limit);
                swapCursor(c);
            }
            try {
                return mUnderlying.moveToPosition(newPosition);
            } catch (IllegalStateException e) {
                return false;
            }
        }

        // /**
        // * @hide
        // * @deprecated
        // */
        // @Override
        // public boolean deleteRow() {
        // return underlying.deleteRow();
        // }
        //
        // /**
        // * @hide
        // * @deprecated
        // */
        // @Override
        // public boolean commitUpdates() {
        // underlying.commitUpdates();
        // onChange(true);
        // return true;
        // }

        @Override
        public String getString(int column) {
            return mUnderlying.getString(column);
        }

        @Override
        public short getShort(int column) {
            return mUnderlying.getShort(column);
        }

        @Override
        public int getInt(int column) {
            return mUnderlying.getInt(column);
        }

        @Override
        public long getLong(int column) {
            return mUnderlying.getLong(column);
        }

        @Override
        public float getFloat(int column) {
            return mUnderlying.getFloat(column);
        }

        @Override
        public double getDouble(int column) {
            return mUnderlying.getDouble(column);
        }

        @Override
        public boolean isNull(int column) {
            return mUnderlying.isNull(column);
        }

        @Override
        public byte[] getBlob(int column) {
            return mUnderlying.getBlob(column);
        }

        @Override
        public String[] getColumnNames() {
            return mColumnNames;
        }

        @Override
        public void deactivate() {
            mUnderlying.deactivate();
            super.deactivate();
        }

        @Override
        public void close() {
            if (mUnderlying != null)
                mUnderlying.close();
            super.close();
        }

        @Override
        public void registerContentObserver(ContentObserver observer) {
            if (mContentObservers == null)
                mContentObservers = new HashSet<ContentObserver>();
            mContentObservers.add(observer);
            if (mUnderlying != null)
                mUnderlying.registerContentObserver(observer);
        }

        @Override
        public void unregisterContentObserver(ContentObserver observer) {
            if (mContentObservers != null)
                mContentObservers.remove(observer);
            if (mUnderlying != null)
                mUnderlying.unregisterContentObserver(observer);
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            if (mDataSetObservers == null)
                mDataSetObservers = new HashSet<DataSetObserver>();
            if (mUnderlying != null)
                mUnderlying.registerDataSetObserver(observer);
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (mDataSetObservers != null)
                mDataSetObservers.remove(observer);
            if (mUnderlying != null)
                mUnderlying.unregisterDataSetObserver(observer);
        }

        @Override
        public boolean requery() {
            if (mUnderlying != null)
                return mUnderlying.requery();
            return true;
        }
    }
}
