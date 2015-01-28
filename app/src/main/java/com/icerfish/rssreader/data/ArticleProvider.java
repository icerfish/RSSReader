package com.icerfish.rssreader.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class ArticleProvider extends ContentProvider {

    public static String TAG = ArticleProvider.class.getSimpleName();

    private ArticleDatabase mDB;

    private static final String AUTHORITY = "com.icerfish.rssreader.data.ArticleProvider";
    public static final int ARTICLES = 100;
    public static final int ARTICLE_ID = 110;

    private static final String ARTICLES_BASE_PATH = "articles";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + ARTICLES_BASE_PATH);

    @Override
    public boolean onCreate() {
        mDB = new ArticleDatabase((getContext()));
        return true;
    }

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, ARTICLES_BASE_PATH, ARTICLES);
        sURIMatcher.addURI(AUTHORITY, ARTICLES_BASE_PATH + "/#", ARTICLE_ID);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ArticleDatabase.TABLE_ARTICLES);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case ARTICLE_ID:
                queryBuilder.appendWhere(ArticleDatabase.ID + "="
                        + uri.getLastPathSegment());
                break;
            case ARTICLES:
                // no filter
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        Cursor cursor = queryBuilder.query(mDB.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDB.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case ARTICLES:
                id = sqlDB.replace(ArticleDatabase.TABLE_ARTICLES, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(ARTICLES_BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDB.getWritableDatabase();
        int rowsAffected = 0;

        switch (uriType) {
            case ARTICLES:
                rowsAffected = sqlDB.delete(ArticleDatabase.TABLE_ARTICLES,
                        selection, selectionArgs);
                Log.i(TAG, "Rows affected " + rowsAffected);
                break;
            case ARTICLE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsAffected = sqlDB.delete(ArticleDatabase.TABLE_ARTICLES,
                            ArticleDatabase.ID + "=" + id, null);
                } else {
                    rowsAffected = sqlDB.delete(ArticleDatabase.TABLE_ARTICLES,
                            selection + " and " + ArticleDatabase.ID + "=" + id,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
