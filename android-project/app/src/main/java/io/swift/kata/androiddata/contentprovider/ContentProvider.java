package io.swift.kata.androiddata.contentprovider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

public class ContentProvider extends android.content.ContentProvider {

    public static final String AUTHORITY = "io.swift.kata.androiddata";
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);;
    private static final String DBNAME = "myLocalDb";

    private DatabaseHelper mOpenHelper;
    private SQLiteDatabase db;

    static {
        sUriMatcher.addURI(AUTHORITY, DatabaseHelper.TABLE, 1);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(
                getContext(),        // the application context
                DBNAME,              // the name of the database)
                null,                // uses the default SQLite cursor
                1                    // the version number
        );
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        db = mOpenHelper.getReadableDatabase();
        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
        qBuilder.setTables(uri.getLastPathSegment());

        Cursor c = qBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        db = mOpenHelper.getWritableDatabase();
        String tableName = uri.getLastPathSegment();

        long id = db.insert(tableName, null, values);

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        db = mOpenHelper.getWritableDatabase();
        String tableName = uri.getLastPathSegment();

        int id = db.delete(tableName, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        return id;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        db = mOpenHelper.getWritableDatabase();
        String tableName = uri.getLastPathSegment();

        int id = db.update(tableName, values, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        return id;
    }
}
