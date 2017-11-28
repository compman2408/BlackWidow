package com.example.blackwidow;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Ryan S on 11/28/2017.
 */

public class NetworkHelper {

    public enum POST_TYPE {
        GET_EXPLOITS
    }

    private static final String TAG = "TAGNetworkHelper";
    public static String LAST_API_REQUEST_RAW_RESPONSE = "";

    private static Context contextToUse;
    private static INetworkPostCallback _postCallback;

    public static void GetExploitsFromAPI(Context context, String postData, INetworkPostCallback postCallback) {
        contextToUse = context;
        _postCallback = postCallback;
        new GetExploitsPostTask().execute(postData);
    }

    /**
     * Implementation of AsyncTask, to fetch the Route Stops data in the background away from
     * the UI thread.
     */
    private static class GetExploitsPostTask extends AsyncTask<String, Void, String> {
        private static final String TAGDT = "AddressDownloadTask";

        @Override
        protected String doInBackground(String... data) {
            Log.i(TAGDT, "Posting Data: " + data[0]);

            try {
                return postToShodanAPI(data[0]);
            } catch (Exception e) {
                return "ERROR: " + e.getMessage();
            }
        }

        /**
         * Uses the logging framework to display the output of the fetch
         * operation in the log fragment.
         */
        @Override
        protected void onPostExecute(String result) {
            // Do something with the response from the website
            Log.i(TAGDT, "Post response download completed...");
            NetworkHelper.LAST_API_REQUEST_RAW_RESPONSE = result;

            if (!result.trim().startsWith("ERROR")) {
                Log.i(TAGDT, "Data successfully downloaded!");
                _postCallback.NetworkPostCallback(POST_TYPE.GET_EXPLOITS, result);
            } else {
                Log.i(TAGDT, "Error downloading Shodan exploits --> " + result.trim());
                Toast.makeText(contextToUse, "Error downloading Shodan exploits --> " + result.trim(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Initiates the fetch operation.
    private static String postToShodanAPI(String postDataString) throws Exception {
        Log.i(TAG, "Posting To Network...");
        InputStream responseStream = null;
        OutputStreamWriter postStream = null;
        String responseStr = "";

        try {
            String postURL = "https://exploits.shodan.io/api/search";
            Log.v(TAG, "POST URL: " + postURL);
            URL url = new URL(postURL);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            // Start the request
            conn.connect();
            Log.v(TAG, "Connected!");

            // Post the data to the website
            postStream = new OutputStreamWriter(conn.getOutputStream());
            postStream.write(postDataString);
            postStream.flush();

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
