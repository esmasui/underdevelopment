
package com.uphyca;

import android.content.Context;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public final class LazyLoading {

    public static final SQLiteQueryBuilder newQueryBuilder(Context context, Uri uri) {
        LazyLoadingSQLiteQueryBuilder queryBuilder = new LazyLoadingSQLiteQueryBuilder(context, uri);
        return queryBuilder;
    }
}
