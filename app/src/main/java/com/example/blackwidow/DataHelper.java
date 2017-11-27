package com.example.blackwidow;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Ryan on 11/26/2017.
 */

public class DataHelper extends SQLiteOpenHelper {
    private Context globalContext;

    // Debug Log Tag
    private static final String TAG = "DataHelperClass";

    // Database Name
    private static final String DB_NAME = "mmdb.db";

    // Database Version
    private static final int DB_VERSION = 1;

    // Database Tables
    public static final String TBL_MOVIES = "Movies";
    public static final String TBL_FORMATS = "Formats";
    public static final String TBL_MOVIE_FORMAT = "MovieFormat";

    // Movies Table Columns
    public static final String TBL_MOVIES_NAME = "Name";
    public static final String TBL_MOVIES_UPC = "UPC";
    public static final String TBL_MOVIES_PPC = "PPC";
    public static final String TBL_MOVIES_CREATED = "Created";

    // Formats Table Columns
    public static final String TBL_FORMATS_NAME = "Name";
    public static final String TBL_FORMATS_CREATED = "Created";

    // MovieFormat Table Columns
    public static final String TBL_MOVIE_FORMAT_MOVIE_ID = "MovieID";
    public static final String TBL_MOVIE_FORMAT_FORMAT_ID = "FormatID";
    public static final String TBL_MOVIE_FORMAT_CREATED = "Created";

    // Common table columns
    public static final String TBL_ID = "_id";

    public DataHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        globalContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Apparently the database hasn't been created yet. Guess I should probably do that now...");

        // Create Table SQL Statements
        final String TBL_MOVIES_CREATE = "CREATE TABLE " + TBL_MOVIES + " (" + TBL_ID + " integer primary key autoincrement, " +
                TBL_MOVIES_NAME + " text, " +
                TBL_MOVIES_UPC + " text, " +
                TBL_MOVIES_PPC + " text, " +
                TBL_MOVIES_CREATED + " integer)";

        // Create Table SQL Statements
        final String TBL_FORMATS_CREATE = "CREATE TABLE " + TBL_FORMATS + " (" + TBL_ID + " integer primary key autoincrement, " +
                TBL_FORMATS_NAME + " text, " +
                TBL_FORMATS_CREATED + " integer)";

        // Create Table SQL Statements
        final String TBL_MOVIE_FORMAT_CREATE = "CREATE TABLE " + TBL_MOVIE_FORMAT + " (" + TBL_ID + " integer primary key autoincrement, " +
                TBL_MOVIE_FORMAT_MOVIE_ID + " integer, " +
                TBL_MOVIE_FORMAT_FORMAT_ID + " integer, " +
                TBL_MOVIE_FORMAT_CREATED + " integer)";

        // Create the database tables
        db.execSQL(TBL_MOVIES_CREATE);
        db.execSQL(TBL_FORMATS_CREATE);
        db.execSQL(TBL_MOVIE_FORMAT_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Looks like we're running on an older version of the database (" + oldVersion + "). Time to upgrade to version " + newVersion + "!!");
    }

    public static long GetDBboolean(boolean bool) {
        if(bool)
            return 1;
        else
            return 0;
    }

    public static boolean GetBoolFromDB(long num) {
        if(num==1)
            return true;
        else
            return false;
    }
}
