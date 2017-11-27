package com.example.blackwidow;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Ryan on 11/26/2017.
 */

public class PhoneDB {
    private static final String TAG = "PhoneDB";

    public static String InsertScanIntoDB(Context context, String scanName) {
        DataHelper data = new DataHelper(context);
        SQLiteDatabase database = data.getWritableDatabase();
        ContentValues values = new ContentValues();
        Long tsLong = System.currentTimeMillis()/1000;

        values.put(DataHelper.SCAN_NAME, scanName);
        values.put(DataHelper.SCAN_TIME_STAMP, tsLong.toString());

        database.close();
        return values.get(DataHelper.SCAN_ID).toString();

    }

    public static String InsertHostIntoDB(Context context, String hostName, String ip, String os, String openPorts, String scanId) {
        /*
        if (device.GetID() != -1) {
            throw new IllegalArgumentException("Host appears to already have been inserted into the database.");
        }
        */
        DataHelper data = new DataHelper(context);
        SQLiteDatabase database = data.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DataHelper.HOST_NAME, hostName);
        values.put(DataHelper.HOST_IP, ip);
        values.put(DataHelper.HOST_OS, os);
        values.put(DataHelper.HOST_OPEN_PORTS, openPorts);
        values.put(DataHelper.HOST_SCAN_FKID, scanId);

        database.close();
        return values.get(DataHelper.HOST_ID).toString();

    }

    public static void InsertExploitIntoDB(Context context, String name, String description, String hostId) {

        DataHelper data = new DataHelper(context);
        SQLiteDatabase database = data.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DataHelper.EXPLOIT_NAME, name);
        values.put(DataHelper.EXPLOIT_DESCRIPTION, description);
        values.put(DataHelper.EXPLOIT_HOST_FKID, hostId);

        database.close();
    }

    // currently working here
    public static ArrayList<Scan> GetScansFromDB(Context context) {
        Log.d(TAG, "Getting Scans from db...");
        DataHelper data = new DataHelper(context);
        Log.d(TAG, "Getting database...");
        SQLiteDatabase database = data.getWritableDatabase();
        ArrayList<Scan> Scans = new ArrayList();

        Log.d(TAG, "Querying db...");
        Cursor cursor = database.rawQuery("SELECT * FROM " + DataHelper.SCANS, null);

        Log.d(TAG, "Iterating through cursor...");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Scan newScan = GetMovieFromCursor(cursor);
            //newMovie.SetFormats(GetFormatsForMovie(context, newMovie, database));
            movies.add(newMovie);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        database.close();
        return movies;
    }

    public static List<Format> GetAllFormatsFromDB(Context context) {
        DataHelper data = new DataHelper(context);
        SQLiteDatabase database = data.getWritableDatabase();
        List<Format> formats = new ArrayList();

        Cursor cursor = database.rawQuery("SELECT * FROM " + DataHelper.HOSTS, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            formats.add(GetFormatFromCursor(cursor));
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        database.close();
        return formats;
    }

    public static List<Format> GetFormatsForMovie(Context context, Movie movie) {
        Log.d(TAG, "Getting formats for movie '" + movie.GetName() + "'...");
        DataHelper data = new DataHelper(context);
        SQLiteDatabase database = data.getWritableDatabase();
        List<Format> formats = new ArrayList();

        Cursor cursor = database.rawQuery("SELECT * FROM " + DataHelper.HOSTS + " f JOIN " + DataHelper.EXPLOITS + " mf ON mf." + DataHelper.HOST_FKID + "=f." + DataHelper.TBL_ID + " WHERE mf." + DataHelper.EXPLOIT_ID + "=" + String.valueOf(movie.GetID()), null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            formats.add(GetFormatFromCursor(cursor));
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        database.close();
        return formats;
    }

    public static List<Format> GetFormatsForMovie(Context context, Movie movie, SQLiteDatabase db) {
        Log.d(TAG, "Getting formats for movie '" + movie.GetName() + "' with open db...");
        List<Format> formats = new ArrayList();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DataHelper.HOSTS + " f JOIN " + DataHelper.EXPLOITS + " mf ON mf." + DataHelper.HOST_FKID + "=f." + DataHelper.TBL_ID + " WHERE mf." + DataHelper.EXPLOIT_ID + "=" + String.valueOf(movie.GetID()), null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            formats.add(GetFormatFromCursor(cursor));
            cursor.moveToNext();
        }
        return formats;
    }

    //region Cursors for data retrieval
    // Use this function to get a HistoryItem object out of a cursor
    private static Scan GetScanFromCursor(Cursor cursor) {
        Scan ScanItem = new Scan(null,null,null,null);

        String id = cursor.getString(cursor.getColumnIndex(DataHelper.SCAN_ID));
        ScanItem.setId(id);
        ScanItem.setName(cursor.getString(cursor.getColumnIndex(DataHelper.SCAN_NAME)));
        ScanItem.setTimeStamp(cursor.getString(cursor.getColumnIndex(DataHelper.SCAN_TIME_STAMP)));



        return DeviceItem;
    }

    private static Device GetHostFromCursor(Cursor cursor) {
        Device hostItem = new Device(null,null,null,null,null);


        return null;
    }

    //
    private static ArrayList<Exploit> GetExploitsFromHostId(Context context, String HostId) {
        Exploit exploitItem = new Exploit(null,null,null);
        DataHelper data = new DataHelper(context);
        SQLiteDatabase database = data.getWritableDatabase();
        ArrayList<Exploit> exploits = new ArrayList<Exploit>();

        Cursor cursor = database.rawQuery("SELECT * FROM " + DataHelper.EXPLOITS + " WHERE Host_FkId=\'" + HostId + "\'", null);
        while (!cursor.isAfterLast()) {
            exploitItem.setId(cursor.getString(cursor.getColumnIndex(DataHelper.EXPLOIT_ID)));
            exploitItem.setName(cursor.getString(cursor.getColumnIndex(DataHelper.EXPLOIT_NAME)));
            exploitItem.setDescription(cursor.getString(cursor.getColumnIndex(DataHelper.EXPLOIT_DESCRIPTION)));
            exploits.add(exploitItem);


        return exploits;
    }


}
