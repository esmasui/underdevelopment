
package com.uphyca.sql;

public interface CountQueryBuilder {

    String buildQuery(String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit);
}
