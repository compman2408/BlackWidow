package com.example.blackwidow;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Contributed to by Ryan and Alex
 */

public class DataHelper extends SQLiteOpenHelper {
    private Context globalContext;

    // Debug Log Tag
    private static final String TAG = "DataHelperClass";

    // Database Name
    private static final String DB_NAME = "nmap.db";

    // Database Version
    private static final int DB_VERSION = 1;

    // Database Tables
    public static final String SCANS = "Scans";
    public static final String HOSTS = "Hosts";
    public static final String EXPLOITS = "Exploits";

    // Scans Table Columns
    public static final String SCAN_ID = "Id";
    public static final String SCAN_NAME = "Scan_Name";
    public static final String SCAN_TIME_STAMP = "Time_Stamp";

    // Hosts Table Columns
    public static final String HOST_ID = "Host_Id";
    public static final String HOST_SCAN_FKID = "Scan_FkId";
    public static final String HOST_IP = "ip";
    public static final String HOST_NAME = "Host_Name";
    public static final String HOST_OS = "Os";
    public static final String HOST_OPEN_PORTS = "Open_Ports";

    // Exploits Table Columns
    public static final String EXPLOIT_ID = "Exploit_Id";
    public static final String EXPLOIT_HOST_FKID = "Host_FkId";
    public static final String EXPLOIT_NAME = "Exploit_Name";
    public static final String EXPLOIT_DESCRIPTION = "Description";

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
        final String SCANS_TABLE_CREATE = "CREATE TABLE " + SCANS + " (" +
                SCAN_ID + " integer primary key autoincrement, " +
                SCAN_NAME + " text, " +
                SCAN_TIME_STAMP + " text)";

        // Create Table SQL Statements
        final String HOSTS_TABLE_CREATE = "CREATE TABLE " + HOSTS + " (" +
                HOST_ID + " integer primary key autoincrement, " +
                HOST_IP + " text, " +
                HOST_NAME + " text, " +
                HOST_OPEN_PORTS + " text, " +
                HOST_OS + " text, " +
                HOST_SCAN_FKID + " integer, foreign key(Scan_FkId) references Scans(Id))";

        // Create Table SQL Statements
        final String EXPLOITS_TABLE_CREATE = "CREATE TABLE " + EXPLOITS + " (" +
                EXPLOIT_ID + " integer primary key autoincrement, " +
                EXPLOIT_NAME + " text, " +
                EXPLOIT_DESCRIPTION + " text, " +
                EXPLOIT_HOST_FKID + " integer, foreign key(Host_FkId) references Hosts(Host_Id))";

        // Create the database tables
        db.execSQL(SCANS_TABLE_CREATE);
        db.execSQL(HOSTS_TABLE_CREATE);
        db.execSQL(EXPLOITS_TABLE_CREATE);
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
