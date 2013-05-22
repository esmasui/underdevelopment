
package com.uphyca;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.database.AbstractCursor;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class LazyLoadingCursor extends AbstractCursor {

    private final class LazyLoadingDataSetObserver extends DataSetObserver {
        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mCount = -1;
            mCursorIndex = -1;
        }
    }

    private final DataSetObserver mDataSetObserver;

    private final Cursor mCounter;
    private final int mBlockSize;

    private final Context mContext;
    private final Uri mContentUri;
    private List<Cursor> mCursors;
    private Cursor mCursor;
    private int mCount;
    private int mCursorIndex;

    private final String[] mColumns;
    private final String mSelection;
    private final String[] mSelectionArgs;
    private final String mGroupBy;
    private final String mHaving;
    private final String mOrderBy;

    private final String[] mColumnNames;

    private List<Operations.Operation> mOperations = new ArrayList<Operations.Operation>();

    private final SQLiteDatabase mDatabase;

    public LazyLoadingCursor(Context context, Uri uri, SQLiteDatabase db, List<Operations.Operation> op, String[] projectionIn, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder, String limit, int blockSize) {
        mContext = context;
        mContentUri = uri;
        mDatabase = db;
        mOperations = op;
        mBlockSize = blockSize;

        mColumns = projectionIn;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mGroupBy = groupBy;
        mHaving = having;
        mOrderBy = sortOrder;

        mCursors = new ArrayList<Cursor>();
        mCursorIndex = -1;

        SQLiteQueryBuilder builder = execOperations(new SQLiteQueryBuilder());
        String rawSql = builder.buildQuery(projectionIn, selection, null, groupBy, having, sortOrder, limit);

        String countSql = String.format("SELECT COUNT(" + (projectionIn != null ? projectionIn[0] : "'X'") + ") COUNT FROM(%s)", rawSql);

        Cursor counter = db.rawQuery(countSql, selectionArgs);

        mDataSetObserver = new LazyLoadingDataSetObserver();
        counter.registerDataSetObserver(mDataSetObserver);

        mCounter = counter;
        mCounter.setNotificationUri(context.getContentResolver(), mContentUri);

        if (projectionIn == null) {
            Cursor cursor = builder.query(db, projectionIn, selection, selectionArgs, groupBy, having, null, "0,0");
            mColumnNames = cursor.getColumnNames();
            cursor.close();
        } else {
            mColumnNames = projectionIn;
        }

        allocate(mContext, mContentUri, db);
    }

    private SQLiteQueryBuilder execOperations(SQLiteQueryBuilder builder) {
        for (Operations.Operation each : mOperations) {
            each.exec(builder);
        }
        return builder;
    }

    private void allocate(Context context, Uri contentUri, SQLiteDatabase db) {
        if (mCounter.isBeforeFirst()) {
            mCounter.moveToFirst();
        }
        mCount = mCounter.getInt(0);
        int size = mCount / mBlockSize;
        if ((mCount - size * mBlockSize) > 0)
            size += 1;

        List<Cursor> oldCursors = mCursors;
        if (oldCursors == null)
            mCursors = new ArrayList<Cursor>(size);

        int oldSize = oldCursors == null ? 0 : oldCursors.size();
        for (int position = 0; position < size; ++position) {
            if (position < oldSize) {
                Cursor c = oldCursors.get(position);
                c.requery();
                continue;
            }
            CursorProxy c = new CursorProxy(null, mBlockSize, position);
            c.setNotificationUri(context.getContentResolver(), contentUri);
            mCursors.add(c);
        }
    }

    @Override
    public int getCount() {
        // return counter.getInt(0);
        return mCount;
    }

    @Override
    public boolean onMove(int oldPosition, int newPosition) {

        if (oldPosition == newPosition && mCursorIndex > -1)
            return true;

        int index = newPosition / mBlockSize;

        if (index != mCursorIndex) {
            mCursor = mCursors.get(index);
            mCursorIndex = index;
        }

        int pos = newPosition - (index * mBlockSize);
        return mCursor.moveToPosition(pos);
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
        for (int i = 0, length = mCursors.size(); i < length; i++) {
            Cursor c = mCursors.get(i);
            if (c != null) {
                c.deactivate();
            }
        }
        mCounter.deactivate();
        super.deactivate();
    }

    @Override
    public void close() {
        for (int i = 0, length = mCursors.size(); i < length; i++) {
            Cursor c = mCursors.get(i);
            if (c == null)
                continue;
            c.close();
        }
        mCounter.close();
        super.close();
    }

    @Override
    public void registerContentObserver(ContentObserver observer) {
        for (int i = 0, length = mCursors.size(); i < length; i++) {
            Cursor c = mCursors.get(i);
            if (c != null) {
                c.registerContentObserver(observer);
            }
        }
        mCounter.registerContentObserver(observer);
    }

    @Override
    public void unregisterContentObserver(ContentObserver observer) {
        for (int i = 0, length = mCursors.size(); i < length; i++) {
            Cursor c = mCursors.get(i);
            if (c != null) {
                c.unregisterContentObserver(observer);
            }
        }
        mCounter.unregisterContentObserver(observer);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        for (int i = 0, length = mCursors.size(); i < length; i++) {
            Cursor c = mCursors.get(i);
            if (c != null) {
                c.registerDataSetObserver(observer);
            }
        }
        mCounter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        for (int i = 0, length = mCursors.size(); i < length; i++) {
            Cursor c = mCursors.get(i);
            if (c != null) {
                c.unregisterDataSetObserver(observer);
            }
        }
        mCounter.unregisterDataSetObserver(observer);
    }

    @Override
    public boolean requery() {
        if (mCounter.requery() == false) {
            return false;
        }

        allocate(mContext, mContentUri, mDatabase);

        // for (int i = 0, length = cursors.size(); i < length; i++) {
        // Cursor c = cursors.get(i);
        // if (c == null) {
        // continue;
        // }
        //
        // if (c.requery() == false) {
        // return false;
        // }
        // }

        return true;
    }

    private final class CursorProxy extends AbstractCursor {

        Set<DataSetObserver> mDataSetObservers;
        Set<ContentObserver> mContentObservers;

        int mAmount;
        int mPosition;
        Cursor mUnderlying;

        public CursorProxy(Cursor cursor, int amount, int position) {
            mUnderlying = cursor;
            mAmount = amount;
            mPosition = position;
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

        @Override
        public int getCount() {
            int offset = mPosition * mAmount;
            int count = LazyLoadingCursor.this.getCount();
            int t = count - offset;
            if (t > mAmount) {
                return mAmount;
            }
            return t;
        }

        @Override
        public boolean onMove(int oldPosition, int newPosition) {
            if (mUnderlying == null) {
                SQLiteQueryBuilder builder = execOperations(new SQLiteQueryBuilder());
                String limit = (mPosition * mAmount) + "," + mAmount;
                SQLiteCursor c = (SQLiteCursor) builder.query(mDatabase, mColumns, mSelection, mSelectionArgs, mGroupBy, mHaving, mOrderBy, limit);
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
