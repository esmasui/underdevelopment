package com.uphyca;

import android.content.Context;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class LazyLoading {

    public static SQLiteQueryBuilder newQueryBuilder(Context context,
                                                     Uri uri) {
        LazyLoadingSQLiteQueryBuilder queryBuilder = new LazyLoadingSQLiteQueryBuilder(context, uri);
        //queryBuilder.setCursorFactory(new LazyLoadingCursorFactory(queryBuilder));

        return queryBuilder;
    }
}
