package com.example.blackwidow;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Contributed to by Ryan and Alex
 * Class used to interact with the SQLite database, allowing for storing/querying scans and hosts
 */

public class PhoneDB {
    private static final String TAG = "PhoneDB";

    public static long InsertScanIntoDB(Context context, String scanName) {
        DataHelper data = new DataHelper(context);
        SQLiteDatabase database = data.getWritableDatabase();
        ContentValues values = new ContentValues();
        Long tsLong = System.currentTimeMillis()/1000;

        values.put(DataHelper.SCAN_NAME, scanName);
        values.put(DataHelper.SCAN_TIME_STAMP, tsLong.toString());

        long id = database.insert(DataHelper.SCANS,null,values);

        database.close();
        return id;

    }

    public static long InsertHostIntoDB(Context context, String hostName, String ip, String os, String openPorts, long scanId) {
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
        Log.wtf(TAG, "insert host into DB");
        Log.wtf(TAG, values.toString());
        long id = database.insert(DataHelper.HOSTS,null,values);

        database.close();
        return id;
    }

    public static void InsertExploitIntoDB(Context context, String name, String description, long hostId) {

        DataHelper data = new DataHelper(context);
        SQLiteDatabase database = data.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DataHelper.EXPLOIT_NAME, name);
        values.put(DataHelper.EXPLOIT_DESCRIPTION, description);
        values.put(DataHelper.EXPLOIT_HOST_FKID, hostId);

        Log.wtf(TAG, "insert exploit into DB");
        Log.wtf(TAG, values.toString());
        database.insert(DataHelper.EXPLOITS,null,values);

        database.close();
    }

    // currently working here
    public static ArrayList<Scan> GetScansFromDB(Context context) {
        Log.d(TAG, "Getting Scans from db...");
        DataHelper data = new DataHelper(context);
        Log.d(TAG, "Getting database...");
        SQLiteDatabase database = data.getWritableDatabase();
        ArrayList<Scan> scans = new ArrayList();

        Log.d(TAG, "Querying db...");
        Cursor cursor = database.rawQuery("SELECT * FROM " + DataHelper.SCANS, null);

        Log.d(TAG, "Iterating through cursor...");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Scan newScan = GetScanFromCursor(context,cursor);
            scans.add(newScan);
            cursor.moveToNext();
            Log.wtf(TAG, newScan.toString());
            Log.wtf(TAG, "next scan");
        }
        // make sure to close the cursor
        cursor.close();
        database.close();
        return scans;
    }


    //region Cursors for data retrieval
    // Use this function to get a HistoryItem object out of a cursor
    private static Scan GetScanFromCursor(Context context, Cursor cursor) {

        Scan ScanItem = new Scan(null,null,null,null);

        String id = cursor.getString(cursor.getColumnIndex(DataHelper.SCAN_ID));
        ScanItem.setId(id);
        ScanItem.setName(cursor.getString(cursor.getColumnIndex(DataHelper.SCAN_NAME)));
        ScanItem.setTimeStamp(cursor.getString(cursor.getColumnIndex(DataHelper.SCAN_TIME_STAMP)));
        ScanItem.setDeviceList(GetHostsFromScanId(context,id));

        return ScanItem;
    }

    private static ArrayList<Device> GetHostsFromScanId(Context context, String scanId) {

        Device hostItem;
        DataHelper data = new DataHelper(context);
        SQLiteDatabase database = data.getWritableDatabase();
        ArrayList<Device> hosts = new ArrayList<Device>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + DataHelper.HOSTS + " WHERE Scan_FkId=\'" + scanId + "\'", null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String id = cursor.getString(cursor.getColumnIndex(DataHelper.HOST_ID));
            hostItem = new Device(null,null,null,null,null,null);
            hostItem.setId(id);
            hostItem.setHostName(cursor.getString(cursor.getColumnIndex(DataHelper.HOST_NAME)));
            hostItem.setIpAddress(cursor.getString((cursor.getColumnIndex(DataHelper.HOST_IP))));
            hostItem.setOperatingSystem(cursor.getString(cursor.getColumnIndex(DataHelper.HOST_OS)));
            hostItem.setOpenPorts(cursor.getString(cursor.getColumnIndex(DataHelper.HOST_OPEN_PORTS)));

            hostItem.setExploits(GetExploitsFromHostId(context,id));
            hosts.add(hostItem);
            cursor.moveToNext();
        }

        cursor.close();
        database.close();
        Log.wtf("SavedScans", "HOST SIZE" + Integer.toString(hosts.size()));
        Log.wtf("SavedScans", "HOST 0" + hosts.get(0).getIpAddress());
        Log.wtf("SavedScans", "HOST LAST" + hosts.get(hosts.size()-1).getIpAddress());
        return hosts;
    }

    //
    private static ArrayList<Exploit> GetExploitsFromHostId(Context context, String HostId) {

        Exploit exploitItem = new Exploit(null,null,null);
        DataHelper data = new DataHelper(context);
        SQLiteDatabase database = data.getWritableDatabase();
        ArrayList<Exploit> exploits = new ArrayList<Exploit>();

        Cursor cursor = database.rawQuery("SELECT * FROM " + DataHelper.EXPLOITS + " WHERE Host_FkId=\'" + HostId + "\'", null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            exploitItem.setId(cursor.getString(cursor.getColumnIndex(DataHelper.EXPLOIT_ID)));
            exploitItem.setName(cursor.getString(cursor.getColumnIndex(DataHelper.EXPLOIT_NAME)));
            exploitItem.setDescription(cursor.getString(cursor.getColumnIndex(DataHelper.EXPLOIT_DESCRIPTION)));
            exploits.add(exploitItem);
            cursor.moveToNext();
        }

        cursor.close();
        database.close();
        return exploits;
    }


}
