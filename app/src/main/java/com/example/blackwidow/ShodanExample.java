package com.example.blackwidow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.net.URLEncoder;

/**
 * Created by Ryan S on 11/28/2017.
 */

public class ShodanExample extends Activity implements INetworkPostCallback {
    private static final String TAG = "TAGShodanExample";

    private ProgressDialog dlgLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the progress dialog to show the user that something is happening
        dlgLoading = new ProgressDialog(this);
        dlgLoading.setTitle("Please Wait...");
        dlgLoading.setCancelable(false);
    }

    private void getExploitsForOS(String os) {
        Log.i(TAG, "Fetch exploits for OS from Shodan API...");
        dlgLoading.setMessage("Getting exploits for OS: " + os);
        dlgLoading.show();

        StringBuilder postData = new StringBuilder();

        try {
            postData.append(URLEncoder.encode("api_key", "UTF-8"));//
            postData.append("=" + URLEncoder.encode("jhadfhhga76tasg3ybe", "UTF-8"));

            postData.append("&" + URLEncoder.encode("os", "UTF-8"));
            postData.append("=" + URLEncoder.encode(os, "UTF-8"));

//            SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//            postData.append("&" + URLEncoder.encode("date", "UTF-8"));
//            postData.append("=" + URLEncoder.encode(s.format(Calendar.getInstance(Locale.US).getTime()), "UTF-8"));

            NetworkHelper.GetExploitsFromAPI(this, postData.toString(), this);
        } catch (Exception ex) {
            Log.i(TAG, "Error starting post: " + ex.getMessage());
        }
    }

    @Override
    public void NetworkPostCallback(NetworkHelper.POST_TYPE x, String jsonResponse) {
        // Dismiss the dialog box telling the user to wait
        if (dlgLoading.isShowing())
            dlgLoading.dismiss();

        // Do something with the response
        if (x == NetworkHelper.POST_TYPE.GET_EXPLOITS) {
            //////////////////////////////////////////////
            //   DO SOMETHING WITH THE RESPONSE HERE    //
            //////////////////////////////////////////////
            Toast.makeText(this, "Response --> " + jsonResponse, Toast.LENGTH_SHORT).show();
        }
    }
}
