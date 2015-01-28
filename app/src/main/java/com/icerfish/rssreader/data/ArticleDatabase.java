package com.icerfish.rssreader.data;

import android.content.Context;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by dylanturney on 26/01/15.
 */
public class ArticleDatabase extends SQLiteOpenHelper{
    public static final String TAG = ArticleDatabase.class
            .getSimpleName();
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "article_data";

    public static final String TABLE_ARTICLES = "articles";
    public static final String ID = "_id";
    public static final String COL_TITLE = "title";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_LINK = "link";
    public static final String COL_PUB_DATE = "pub_date";
    public static final String COL_THUMBNAIL_SMALL = "thumbnail_small";
    public static final String COL_THUMBNAIL_LARGE = "thumbnail_large";


    private static final String CREATE_TABLE = "CREATE TABLE " +
            TABLE_ARTICLES + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_TITLE
            + " TEXT," + COL_DESCRIPTION+ " TEXT," + COL_LINK + " TEXT, "
            + COL_PUB_DATE + " TEXT, " + COL_THUMBNAIL_SMALL + " TEXT, "
            + COL_THUMBNAIL_LARGE + " TEXT" + ")";

    private static final String DB_SCHEMA = CREATE_TABLE;

    public ArticleDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_SCHEMA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database. Existing contents will be lost. ["
                + oldVersion + "]->[" + newVersion + "]");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLES);
        onCreate(db);
    }
}
