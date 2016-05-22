package io.swift.kata.androiddata.contentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {


    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_STATUS = "STATUS";
    public static final String COLUMN_ID = "_ID";
    public static final int NO_PENDING_ACTION = 0;
    public static final int PENDING_INSERTION = 1;
    public static final int PENDING_DELETION = 2;
    public static final String TABLE = "name";
    private static final String SQL_CREATE_MAIN = "CREATE TABLE " +
            TABLE + " " +
            "( " +
            COLUMN_ID + " INTEGER PRIMARY KEY, " +
            COLUMN_NAME+" TEXT, " +
            COLUMN_STATUS +" INTEGER )";


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MAIN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
