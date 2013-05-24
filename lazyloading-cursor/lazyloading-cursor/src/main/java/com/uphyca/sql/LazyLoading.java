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

package com.uphyca.sql;

import android.content.Context;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public final class LazyLoading {

    public static final SQLiteQueryBuilder newQueryBuilder(Context context, Uri uri, CountQueryBuilder builder) {
        return new LazyLoadingSQLiteQueryBuilder(context, uri, builder);
    }

    public static final SQLiteQueryBuilder newQueryBuilder(Context context, Uri uri, CountQueryBuilder builder, int blockSize) {
        return new LazyLoadingSQLiteQueryBuilder(context, uri, builder, blockSize);
    }

    public static final SQLiteQueryBuilder newQueryBuilder(Context context, Uri uri) {
        return new LazyLoadingSQLiteQueryBuilder(context, uri, null);
    }

    public static final SQLiteQueryBuilder newQueryBuilder(Context context, Uri uri, int blockSize) {
        return new LazyLoadingSQLiteQueryBuilder(context, uri, null, blockSize);
    }

}