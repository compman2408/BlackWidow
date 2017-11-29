package com.example.blackwidow;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Ryan on 11/28/2017.
 */

public class NetworkHelper {
    private static final String TAG = "NetworkHelper";

    // Initiates the fetch operation.
    public static String postToNetwork(String postURL, String postDataString) throws Exception {
        Log.i(TAG, "Posting To Network...");
        InputStream responseStream = null;
        OutputStreamWriter postStream = null;
        String responseStr = "";

        try {
            String getURL = postURL + "?" + postDataString;
            Log.v(TAG, "POST URL: " + getURL);
            URL url = new URL(getURL);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Start the request
            conn.connect();
            Log.v(TAG, "Connected!");

            // Post the data to the website
//            postStream = new OutputStreamWriter(conn.getOutputStream());
//            postStream.write(postDataString);
//            postStream.flush();

            // Read the response
            responseStream = conn.getInputStream();
            Log.v(TAG, "Reading stream...");
            BufferedReader r = new BufferedReader(new InputStreamReader(responseStream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line + "\n");
            }
            responseStr = total.toString();
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error occurred when posting to network --> " + e.getMessage());
            throw e;
        }
        finally {
            if (responseStream != null) {
                responseStream.close();
                responseStream = null;
            }
            if (postStream != null) {
                postStream.close();
                postStream = null;
            }
        }

        return responseStr;
    }
}
