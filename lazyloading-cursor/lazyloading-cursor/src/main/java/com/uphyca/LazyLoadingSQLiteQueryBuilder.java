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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.CancellationSignal;

class LazyLoadingSQLiteQueryBuilder extends SQLiteQueryBuilder {

    private static final int DEFAULT_BLOCK_SIZE = 256;

    private final List<Operations.Operation> mOperations = new ArrayList<Operations.Operation>();
    private final Context mContext;
    private final Uri mUri;
    private final int mBlockSize;

    public LazyLoadingSQLiteQueryBuilder(Context context, Uri uri) {
        this(context, uri, DEFAULT_BLOCK_SIZE);
    }

    public LazyLoadingSQLiteQueryBuilder(Context context, Uri uri, int blockSize) {
        mContext = context;
        mUri = uri;
        mBlockSize = blockSize;
    }

    /*
     * (non-Javadoc)
     * @see android.database.sqlite.SQLiteQueryBuilder#getTables()
     */
    @Override
    public String getTables() {
        return super.getTables();
    }

    /*
     * (non-Javadoc)
     * @see
     * android.database.sqlite.SQLiteQueryBuilder#appendWhere(java.lang.CharSequence
     * )
     */
    @Override
    public void appendWhere(CharSequence inWhere) {
        super.appendWhere(inWhere);
        mOperations.add(new Operations.AppendWhere(inWhere));
    }

    /*
     * (non-Javadoc)
     * @see
     * android.database.sqlite.SQLiteQueryBuilder#appendWhereEscapeString(java
     * .lang.String)
     */
    @Override
    public void appendWhereEscapeString(String inWhere) {
        super.appendWhereEscapeString(inWhere);
        mOperations.add(new Operations.AppendWhereEscapeString(inWhere));
    }

    /*
     * (non-Javadoc)
     * @see
     * android.database.sqlite.SQLiteQueryBuilder#buildQuery(java.lang.String[],
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @SuppressLint("NewApi")
    @Override
    public String buildQuery(String[] projectionIn, String selection, String groupBy, String having, String sortOrder, String limit) {
        return super.buildQuery(projectionIn, selection, groupBy, having, sortOrder, limit);
    }

    /*
     * (non-Javadoc)
     * @see
     * android.database.sqlite.SQLiteQueryBuilder#buildQuery(java.lang.String[],
     * java.lang.String, java.lang.String[], java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    @Deprecated
    public String buildQuery(String[] projectionIn, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder, String limit) {
        return super.buildQuery(projectionIn, selection, selectionArgs, groupBy, having, sortOrder, limit);
    }

    /*
     * (non-Javadoc)
     * @see
     * android.database.sqlite.SQLiteQueryBuilder#buildUnionSubQuery(java.lang
     * .String, java.lang.String[], java.util.Set, int, java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @SuppressLint("NewApi")
    @Override
    public String buildUnionSubQuery(String typeDiscriminatorColumn, String[] unionColumns, Set<String> columnsPresentInTable, int computedColumnsOffset, String typeDiscriminatorValue, String selection, String groupBy, String having) {
        return super.buildUnionSubQuery(typeDiscriminatorColumn, unionColumns, columnsPresentInTable, computedColumnsOffset, typeDiscriminatorValue, selection, groupBy, having);
    }

    /*
     * (non-Javadoc)
     * @see
     * android.database.sqlite.SQLiteQueryBuilder#buildUnionSubQuery(java.lang
     * .String, java.lang.String[], java.util.Set, int, java.lang.String,
     * java.lang.String, java.lang.String[], java.lang.String, java.lang.String)
     */
    @Override
    @Deprecated
    public String buildUnionSubQuery(String typeDiscriminatorColumn, String[] unionColumns, Set<String> columnsPresentInTable, int computedColumnsOffset, String typeDiscriminatorValue, String selection, String[] selectionArgs, String groupBy, String having) {
        return super.buildUnionSubQuery(typeDiscriminatorColumn, unionColumns, columnsPresentInTable, computedColumnsOffset, typeDiscriminatorValue, selection, selectionArgs, groupBy, having);
    }

    /*
     * (non-Javadoc)
     * @see
     * android.database.sqlite.SQLiteQueryBuilder#buildUnionQuery(java.lang.
     * String[], java.lang.String, java.lang.String)
     */
    @Override
    public String buildUnionQuery(String[] subQueries, String sortOrder, String limit) {
        return super.buildUnionQuery(subQueries, sortOrder, limit);
    }

    /*
     * (non-Javadoc)
     * @see
     * android.database.sqlite.SQLiteQueryBuilder#query(android.database.sqlite
     * .SQLiteDatabase, java.lang.String[], java.lang.String,
     * java.lang.String[], java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String, android.os.CancellationSignal)
     */
    @Override
    public Cursor query(SQLiteDatabase db, String[] projectionIn, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder, String limit, CancellationSignal cancellationSignal) {
        return new LazyLoadingCursor(mContext, mUri, db, mOperations, projectionIn, selection, selectionArgs, groupBy, having, sortOrder, limit, mBlockSize);
    }

    /*
     * (non-Javadoc)
     * @see
     * android.database.sqlite.SQLiteQueryBuilder#query(android.database.sqlite
     * .SQLiteDatabase, java.lang.String[], java.lang.String,
     * java.lang.String[], java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public Cursor query(SQLiteDatabase db, String[] projectionIn, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder, String limit) {
        return query(db, projectionIn, selection, selectionArgs, groupBy, having, sortOrder, limit, null);
    }

    /*
     * (non-Javadoc)
     * @see
     * android.database.sqlite.SQLiteQueryBuilder#query(android.database.sqlite
     * .SQLiteDatabase, java.lang.String[], java.lang.String,
     * java.lang.String[], java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Cursor query(SQLiteDatabase db, String[] projectionIn, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder) {
        return query(db, projectionIn, selection, selectionArgs, groupBy, having, sortOrder, null, null);
    }

    /*
     * (non-Javadoc)
     * @see
     * android.database.sqlite.SQLiteQueryBuilder#setCursorFactory(android.database
     * .sqlite.SQLiteDatabase.CursorFactory)
     */
    @Override
    public void setCursorFactory(CursorFactory factory) {
        super.setCursorFactory(factory);
        mOperations.add(new Operations.SetCursorFactory(factory));
    }

    /*
     * (non-Javadoc)
     * @see android.database.sqlite.SQLiteQueryBuilder#setDistinct(boolean)
     */
    @Override
    public void setDistinct(boolean distinct) {
        super.setDistinct(distinct);
        mOperations.add(new Operations.SetDistinct(distinct));
    }

    /*
     * (non-Javadoc)
     * @see
     * android.database.sqlite.SQLiteQueryBuilder#setTables(java.lang.String)
     */
    @Override
    public void setTables(String inTables) {
        super.setTables(inTables);
        mOperations.add(new Operations.SetTables(inTables));
    }

    /*
     * (non-Javadoc)
     * @see
     * android.database.sqlite.SQLiteQueryBuilder#setProjectionMap(java.util
     * .Map)
     */
    @Override
    public void setProjectionMap(Map<String, String> columnMap) {
        super.setProjectionMap(columnMap);
        mOperations.add(new Operations.SetProjectionMap(columnMap));
    }

    /*
     * (non-Javadoc)
     * @see android.database.sqlite.SQLiteQueryBuilder#setStrict(boolean)
     */
    @SuppressLint("NewApi")
    @Override
    public void setStrict(boolean flag) {
        super.setStrict(flag);
        mOperations.add(new Operations.SetStrict(flag));
    }
}
