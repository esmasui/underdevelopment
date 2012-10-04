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
import android.os.CancellationSignal;

public class LazyLoadingCursor extends AbstractCursor {

    class LazyLoadingDataSetObserver extends DataSetObserver {

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            count = -1;
            cursorIndex = -1;
        }
    }

    private final DataSetObserver dataSetObserver;

    private final Cursor counter;
    private final int blockSize;

    private final Context context;
    private final Uri contentUri;
    private List<Cursor> cursors;
    private Cursor cursor;
    private int count;
    private int cursorIndex;

    private final String[] columns;
    private final String selection;
    private final String[] selectionArgs;
    private final String groupBy;
    private final String having;
    private final String orderBy;

    private final String[] columnNames;

    private List<Operations.Operation> op = new ArrayList<Operations.Operation>();

    private final SQLiteDatabase db;

    public LazyLoadingCursor(Context context,
                             Uri uri,
                             SQLiteDatabase db,
                             List<Operations.Operation> op,
                             String[] projectionIn,
                             String selection,
                             String[] selectionArgs,
                             String groupBy,
                             String having,
                             String sortOrder,
                             CancellationSignal cancellationSignal,
                             int blockSize) {
        this.context = context;
        this.contentUri = uri;
        this.db = db;
        this.op = op;
        this.blockSize = blockSize;

        this.columns = projectionIn;
        this.selection = selection;
        this.selectionArgs = selectionArgs;
        this.groupBy = groupBy;
        this.having = having;
        this.orderBy = sortOrder;

        cursors = new ArrayList<Cursor>();
        cursorIndex = -1;

        db.setPageSize(blockSize);
        
        SQLiteQueryBuilder builder = execOperations(new SQLiteQueryBuilder());
        String sql = builder.buildQuery(projectionIn, selection, groupBy, having, sortOrder, null);

        String countSql = String.format("SELECT COUNT(*) COUNT FROM(%s)", sql);

        Cursor counter = db.rawQuery(countSql, selectionArgs, cancellationSignal);

        dataSetObserver = new LazyLoadingDataSetObserver();
        counter.registerDataSetObserver(dataSetObserver);

        this.counter = counter;
        this.counter.setNotificationUri(context.getContentResolver(), contentUri);

        Cursor cursor = builder.query(db, projectionIn, selection, selectionArgs, groupBy, having, sortOrder, "0,0");
        columnNames = cursor.getColumnNames();
        cursor.close();

        allocate(this.context, this.contentUri, db);
    }

    private SQLiteQueryBuilder execOperations(SQLiteQueryBuilder builder) {
        for (Operations.Operation each : op) {
            each.exec(builder);
        }
        return builder;
    }

    private void allocate(Context context,
                          Uri contentUri,
                          SQLiteDatabase db) {
        if (counter.isBeforeFirst()) {
            counter.moveToFirst();
        }
        count = counter.getInt(0);
        int size = count / blockSize;
        if ((count - size * blockSize) > 0)
            size += 1;

        List<Cursor> oldCursors = cursors;
        if (oldCursors == null)
            cursors = new ArrayList<Cursor>(size);

        int oldSize = oldCursors == null ? 0 : oldCursors.size();
        for (int position = 0; position < size; ++position) {
            if (position < oldSize) {
                Cursor c = oldCursors.get(position);
                c.requery();
                continue;
            }
            CursorProxy c = new CursorProxy(null, blockSize, position);
            c.setNotificationUri(context.getContentResolver(), contentUri);
            cursors.add(c);
        }
    }

    @Override
    public int getCount() {
        // return counter.getInt(0);
        return count;
    }

    @Override
    public boolean onMove(int oldPosition,
                          int newPosition) {

        if (oldPosition == newPosition && cursorIndex > -1)
            return true;

        int index = newPosition / blockSize;

        if (index != cursorIndex) {
            cursor = cursors.get(index);
            cursorIndex = index;
        }

        int pos = newPosition - (index * blockSize);
        return cursor.moveToPosition(pos);
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
        return cursor.getString(column);
    }

    @Override
    public short getShort(int column) {
        return cursor.getShort(column);
    }

    @Override
    public int getInt(int column) {
        return cursor.getInt(column);
    }

    @Override
    public long getLong(int column) {
        return cursor.getLong(column);
    }

    @Override
    public float getFloat(int column) {
        return cursor.getFloat(column);
    }

    @Override
    public double getDouble(int column) {
        return cursor.getDouble(column);
    }

    @Override
    public boolean isNull(int column) {
        return cursor.isNull(column);
    }

    @Override
    public byte[] getBlob(int column) {
        return cursor.getBlob(column);
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
        return columnNames;
    }

    @Override
    public void deactivate() {
        for (int i = 0, length = cursors.size(); i < length; i++) {
            Cursor c = cursors.get(i);
            if (c != null) {
                c.deactivate();
            }
        }
        counter.deactivate();
        super.deactivate();
    }

    @Override
    public void close() {
        for (int i = 0, length = cursors.size(); i < length; i++) {
            Cursor c = cursors.get(i);
            if (c == null)
                continue;
            c.close();
        }
        counter.close();
        super.close();
    }

    @Override
    public void registerContentObserver(ContentObserver observer) {
        for (int i = 0, length = cursors.size(); i < length; i++) {
            Cursor c = cursors.get(i);
            if (c != null) {
                c.registerContentObserver(observer);
            }
        }
        counter.registerContentObserver(observer);
    }

    @Override
    public void unregisterContentObserver(ContentObserver observer) {
        for (int i = 0, length = cursors.size(); i < length; i++) {
            Cursor c = cursors.get(i);
            if (c != null) {
                c.unregisterContentObserver(observer);
            }
        }
        counter.unregisterContentObserver(observer);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        for (int i = 0, length = cursors.size(); i < length; i++) {
            Cursor c = cursors.get(i);
            if (c != null) {
                c.registerDataSetObserver(observer);
            }
        }
        counter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        for (int i = 0, length = cursors.size(); i < length; i++) {
            Cursor c = cursors.get(i);
            if (c != null) {
                c.unregisterDataSetObserver(observer);
            }
        }
        counter.unregisterDataSetObserver(observer);
    }

    @Override
    public boolean requery() {
        if (counter.requery() == false) {
            return false;
        }

        allocate(context, contentUri, db);

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

    private class CursorProxy extends AbstractCursor {

        Set<DataSetObserver> dataSetObservers;
        Set<ContentObserver> contentObservers;

        int amount;
        int position;
        Cursor underlying;

        public CursorProxy(Cursor cursor,
                           int amount,
                           int position) {
            underlying = cursor;
            this.amount = amount;
            this.position = position;
        }

        void swapCursor(Cursor cursor) {
            if (contentObservers != null) {
                for (ContentObserver observer : contentObservers)
                    cursor.registerContentObserver(observer);
            }
            if (dataSetObservers != null) {
                for (DataSetObserver observer : dataSetObservers)
                    cursor.registerDataSetObserver(observer);
            }
            underlying = cursor;
        }

        @Override
        public int getCount() {
            int offset = position * amount;
            int count = LazyLoadingCursor.this.getCount();
            int t = count - offset;
            if (t > amount) {
                return amount;
            }
            return t;
        }

        @Override
        public boolean onMove(int oldPosition,
                              int newPosition) {
            if (underlying == null) {
                SQLiteQueryBuilder builder = execOperations(new SQLiteQueryBuilder());
                String limit = (position * amount) + "," + amount;
                SQLiteCursor c = (SQLiteCursor) builder.query(db, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
                swapCursor(c);
            }
            try {
                return underlying.moveToPosition(newPosition);
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
            return underlying.getString(column);
        }

        @Override
        public short getShort(int column) {
            return underlying.getShort(column);
        }

        @Override
        public int getInt(int column) {
            return underlying.getInt(column);
        }

        @Override
        public long getLong(int column) {
            return underlying.getLong(column);
        }

        @Override
        public float getFloat(int column) {
            return underlying.getFloat(column);
        }

        @Override
        public double getDouble(int column) {
            return underlying.getDouble(column);
        }

        @Override
        public boolean isNull(int column) {
            return underlying.isNull(column);
        }

        @Override
        public byte[] getBlob(int column) {
            return underlying.getBlob(column);
        }

        @Override
        public String[] getColumnNames() {
            return columnNames;
        }

        @Override
        public void deactivate() {
            underlying.deactivate();
            super.deactivate();
        }

        @Override
        public void close() {
            if (underlying != null)
                underlying.close();
            super.close();
        }

        @Override
        public void registerContentObserver(ContentObserver observer) {
            if (contentObservers == null)
                contentObservers = new HashSet<ContentObserver>();
            contentObservers.add(observer);
            if (underlying != null)
                underlying.registerContentObserver(observer);
        }

        @Override
        public void unregisterContentObserver(ContentObserver observer) {
            if (contentObservers != null)
                contentObservers.remove(observer);
            if (underlying != null)
                underlying.unregisterContentObserver(observer);
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            if (dataSetObservers == null)
                dataSetObservers = new HashSet<DataSetObserver>();
            if (underlying != null)
                underlying.registerDataSetObserver(observer);
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (dataSetObservers != null)
                dataSetObservers.remove(observer);
            if (underlying != null)
                underlying.unregisterDataSetObserver(observer);
        }

        @Override
        public boolean requery() {
            if (underlying != null)
                return underlying.requery();
            return true;
        }
    }
}