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
/*
    public static void InsertMovieIntoDB(Context context, Movie movie) {
        if (movie.GetID() != -1) {
            throw new IllegalArgumentException("Movie appears to already have been inserted into the database.");
        }
        DataHelper data = new DataHelper(context);
        SQLiteDatabase database = data.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DataHelper.TBL_MOVIES_NAME, movie.GetName());
        values.put(DataHelper.TBL_MOVIES_UPC, movie.GetUPC());
        values.put(DataHelper.TBL_MOVIES_PPC, movie.GetPPC());
        long updatedTime = Calendar.getInstance().getTime().getTime();
        movie.SetCreatedTime(updatedTime);
        values.put(DataHelper.TBL_MOVIES_CREATED, updatedTime);

        long insertId = database.insert(DataHelper.TBL_MOVIES, null, values);
        movie.SetID(insertId);

        if (movie.GetID() != 0) {
            Log.d(TAG, "A new movie has been added with an ID of " + insertId);
            for (int i = 0; i < movie.GetFormats().size(); i++) {
                Format item = movie.GetFormats().get(i);
                ContentValues values2 = new ContentValues();
                values2.put(DataHelper.TBL_MOVIE_FORMAT_FORMAT_ID, item.GetID());
                values2.put(DataHelper.TBL_MOVIE_FORMAT_MOVIE_ID, movie.GetID());
                values2.put(DataHelper.TBL_MOVIE_FORMAT_CREATED, updatedTime);

                long insertID2 = database.insert(DataHelper.TBL_MOVIE_FORMAT, null, values2);
                item.SetID(insertID2);
            }
        } else {
            Log.d(TAG, "A new movie has attempted to be added, but was unsuccessful.");
        }

        database.close();
    }

    public static void InsertFormatIntoDB(Context context, Format format) {
        if (format.GetID() != -1) {
            throw new IllegalArgumentException("Format appears to already have been inserted into the database.");
        }
        DataHelper data = new DataHelper(context);
        SQLiteDatabase database = data.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DataHelper.TBL_FORMATS_NAME, format.GetName());
        long updatedTime = Calendar.getInstance().getTime().getTime();
        format.SetCreatedTime(updatedTime);
        values.put(DataHelper.TBL_FORMATS_CREATED, updatedTime);

        long insertId = database.insert(DataHelper.TBL_FORMATS, null, values);
        format.SetID(insertId);
        database.close();
    }

    public static void InsertFormatIntoDB(Context context, Format format, SQLiteDatabase db) {
        if (format.GetID() != -1) {
            throw new IllegalArgumentException("Format appears to already have been inserted into the database.");
        }

        ContentValues values = new ContentValues();
        values.put(DataHelper.TBL_FORMATS_NAME, format.GetName());
        long updatedTime = Calendar.getInstance().getTime().getTime();
        format.SetCreatedTime(updatedTime);
        values.put(DataHelper.TBL_FORMATS_CREATED, updatedTime);

        long insertId = db.insert(DataHelper.TBL_FORMATS, null, values);
        format.SetID(insertId);
    }

    public static List<Movie> GetMoviesFromDB(Context context) {
        Log.d(TAG, "Getting movies from db...");
        DataHelper data = new DataHelper(context);
        Log.d(TAG, "Getting database...");
        SQLiteDatabase database = data.getWritableDatabase();
        List<Movie> movies = new ArrayList();

        Log.d(TAG, "Querying db...");
        Cursor cursor = database.rawQuery("SELECT * FROM " + DataHelper.TBL_MOVIES, null);

        Log.d(TAG, "Iterating through cursor...");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Movie newMovie = GetMovieFromCursor(cursor);
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

        Cursor cursor = database.rawQuery("SELECT * FROM " + DataHelper.TBL_FORMATS, null);

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

        Cursor cursor = database.rawQuery("SELECT * FROM " + DataHelper.TBL_FORMATS + " f JOIN " + DataHelper.TBL_MOVIE_FORMAT + " mf ON mf." + DataHelper.TBL_MOVIE_FORMAT_FORMAT_ID + "=f." + DataHelper.TBL_ID + " WHERE mf." + DataHelper.TBL_MOVIE_FORMAT_MOVIE_ID + "=" + String.valueOf(movie.GetID()), null);

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

        Cursor cursor = db.rawQuery("SELECT * FROM " + DataHelper.TBL_FORMATS + " f JOIN " + DataHelper.TBL_MOVIE_FORMAT + " mf ON mf." + DataHelper.TBL_MOVIE_FORMAT_FORMAT_ID + "=f." + DataHelper.TBL_ID + " WHERE mf." + DataHelper.TBL_MOVIE_FORMAT_MOVIE_ID + "=" + String.valueOf(movie.GetID()), null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            formats.add(GetFormatFromCursor(cursor));
            cursor.moveToNext();
        }
        return formats;
    }

    //region Cursors for data retrieval
    // Use this function to get a HistoryItem object out of a cursor
    private static Movie GetMovieFromCursor(Cursor cursor) {
        Movie movieItem = new Movie();
        movieItem.SetID(cursor.getLong(cursor.getColumnIndex(DataHelper.TBL_ID)));
        movieItem.SetName(cursor.getString(cursor.getColumnIndex(DataHelper.TBL_MOVIES_NAME)));
        movieItem.SetUPC(cursor.getString(cursor.getColumnIndex(DataHelper.TBL_MOVIES_UPC)));
        movieItem.SetPPC(cursor.getString(cursor.getColumnIndex(DataHelper.TBL_MOVIES_PPC)));
        movieItem.SetCreatedTime(cursor.getLong(cursor.getColumnIndex(DataHelper.TBL_MOVIES_CREATED)));
        return movieItem;
    }

    // Use this function to get a Format object out of a cursor
    private static Format GetFormatFromCursor(Cursor cursor) {
        Format format = new Format();
        format.SetID(cursor.getLong(cursor.getColumnIndex(DataHelper.TBL_ID)));
        format.SetName(cursor.getString(cursor.getColumnIndex(DataHelper.TBL_FORMATS_NAME)));
        format.SetCreatedTime(cursor.getLong(cursor.getColumnIndex(DataHelper.TBL_FORMATS_CREATED)));
        return format;
    }
    //endregion
*/
}
