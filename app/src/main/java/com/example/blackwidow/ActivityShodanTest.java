package com.example.blackwidow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URLEncoder;

public class ActivityShodanTest extends Activity implements IShodanPostCallback {
    private static final String TAG = "ActivityShodanTest";

    private ProgressDialog dlgLoading;

    private EditText txtShodanOs;
    private TextView txtShodanResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shodan_test);

        // Set up the progress dialog to show the user that something is happening
        dlgLoading = new ProgressDialog(this);
        dlgLoading.setTitle("Please Wait...");
        dlgLoading.setCancelable(false);

        txtShodanOs = (EditText) findViewById(R.id.txtShodanOs);
        txtShodanResults = (TextView) findViewById(R.id.txtShodanResults);
    }

    public void btnShodanTestExec_OnClick(View view) {
        getExploitsForOS(this, txtShodanOs.getText().toString(), 1);
        txtShodanOs.setText("");
    }

    public void getExploitsForOS(Context context, String os, long hostID) {
        Log.i(TAG, "Fetch exploits for OS from Shodan API...");
        dlgLoading.setMessage("Getting exploits for OS: " + os);
        dlgLoading.show();

        StringBuilder postData = new StringBuilder();

        try {

            postData.append(URLEncoder.encode("query", "UTF-8"));
            postData.append("=" + URLEncoder.encode(os, "UTF-8"));

            postData.append("&" + URLEncoder.encode("key", "UTF-8"));//
            postData.append("=" + URLEncoder.encode("ofqxN4bGWWDA5GwDEPZNMoEiddgBBZ9B", "UTF-8"));

            new GetExploitsPostTask().execute(new ShodanPostRequest(this, postData.toString(), hostID, this));
        } catch (Exception ex) {
            Log.i(TAG, "Error starting post: " + ex.getMessage());
        }
    }

    @Override
    public void ShodanPostCallback(String jsonResponse, long hostID) {
        // Dismiss the dialog box telling the user to wait
        if (dlgLoading.isShowing())
            dlgLoading.dismiss();

        // Do something with the response
        txtShodanResults.setText(jsonResponse);
        Toast.makeText(this, "Response --> " + jsonResponse, Toast.LENGTH_SHORT).show();
    }
}
