package com.example.blackwidow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * Contributed to by Ryan, Alex, Bobak
 * Main page activity that has buttons that are linked to activities for the ShodanTest, Terminal nmap, EZ Scan, and Saved Scans
 */

public class ActivityMain extends Activity implements IAsyncZipFileProcessingCallback {
    private static final String TAG = "ActivityMain";

    public static  String appDataDirectory;
    public static Map<Utils.Executable, String> executableFileLocations;
    public static String binaryFileLocation;
    public static DataHelper db;

    private Button btnScanNetwork;
    private AnimationDrawable btnAnimation;
    private ProgressDialog dlgLoading;
    private Button btnViewScanHistory;

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
//        TextView tv = (TextView) findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());

        // Start the scan button animation
        btnScanNetwork = (Button) findViewById(R.id.btnScanNetwork);
//        btnAnimation = (AnimationDrawable) btnScanNetwork.getBackground();

        btnViewScanHistory = (Button) findViewById(R.id.btnScanHistory);

        btnAnimation = new AnimationScan(this, R.drawable.btn_scan_frame0);
        btnScanNetwork.setBackground(btnAnimation);

        // Set the directories to the important stuff
        appDataDirectory = getApplicationContext().getFilesDir().getPath();
        // WARNING: The following directory MUST match the directory that is inside the zip file.
        //          DO NOT change the following directory if the directory in the zip file isn't changed.
        binaryFileLocation = appDataDirectory + "/bin";
        // Add files to copy
        executableFileLocations = new HashMap<>();
        executableFileLocations.put(Utils.Executable.NCAT, binaryFileLocation + "/ncat");
        executableFileLocations.put(Utils.Executable.NDIFF, binaryFileLocation + "/ndiff");
        executableFileLocations.put(Utils.Executable.NMAP, binaryFileLocation + "/nmap");
        executableFileLocations.put(Utils.Executable.NPING, binaryFileLocation + "/nping");

        // Set up the progress dialog to show the user that something is happening
        dlgLoading = new ProgressDialog(this);
        dlgLoading.setTitle("Please Wait...");
        dlgLoading.setCancelable(false);
        dlgLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        dlgLoading.setMessage("Copying zip file to local storage...");
        dlgLoading.setProgress(5);
        dlgLoading.show();
        // Copy the zip file from the raw resources to local storage
        String zipLoc = copyZipFileToLocalStorage();

        dlgLoading.setMessage("Unpacking the zip file...");
        dlgLoading.setProgress(10);
        // Use the async process task in the Utils class to do this in a separate thread
        // This way the app can load and just show the user what's going on
        Utils.ProcessZipFileAsync(this, zipLoc, this);

        // Unzip the main fip file
        //unzipMainNmapFile(zipLoc);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!btnAnimation.isRunning())
            btnAnimation.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (btnAnimation.isRunning())
            btnAnimation.stop();
    }

    private String copyZipFileToLocalStorage() {
        String zipLocation = appDataDirectory + "/android_nmap.zip";
        File file = new File(zipLocation);
        Log.d(TAG, "Checking if zip file exists...");
        if (!file.exists()) {
            Log.d(TAG, "Zip file doesn't exist yet...");
            // Copy the zip file from the raw resources to the local Android file system
            try {
                Log.d(TAG, "Copying zip file to local storage...");
                copyBinFileToInternalStorage(R.raw.android_nmap, zipLocation);
                Log.d(TAG, "Zip file copied!");
                // set permissions to 666 (Read and Write for everyone) for all files
                // set permissions to 777 (Read, Write, and Execute for everyone) for main executables
            } catch (Exception ex) {
                Log.d(TAG, "Error copying zip file to storage: " + ex.getMessage());
                ex.printStackTrace();
            }
            try {
                Log.d(TAG, "Making zip file readable...");
                Process process = Runtime.getRuntime().exec("chmod 666 " + zipLocation);
                process.waitFor();
                Log.d(TAG, "Zip file readable!");
            } catch (Exception ex) {
                Log.d(TAG, "Error marking file as readable: " + ex.getMessage());
                ex.printStackTrace();
            }
        } else {
            Log.d(TAG, "Zip file already exists!");
        }
        return zipLocation;
    }

    private void copyBinFileToInternalStorage(int id, String path) throws IOException {
        InputStream in = this.getResources().openRawResource(id);
        FileOutputStream out = new FileOutputStream(path);
        byte[] buff = new byte[1024];
        int read = 0;
        try {
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
        } finally {
            in.close();
            out.close();
        }
    }

    public void btnScanNetwork_OnClick(View view) {
        //startActivity(new Intent(this, ActivityScan.class));
        startActivity(new Intent(this, ActivityEZScan.class));
    }

    public void btnTerminal_OnClick(View view) {
        startActivity(new Intent(this, ActivityTerminal.class));
    }

    public void btnViewScanHistory_OnClick(View view) {
        startActivity(new Intent(this, SavedScans.class));
    }

    @Override
    public void ZipFileProcessingProgressUpdate(int progress, String message) {
        dlgLoading.setMessage(message);
        dlgLoading.setProgress(progress);
    }

    @Override
    public void ZipFileProcessingCompleted(boolean isError, String message) {
        if (dlgLoading.isShowing())
            dlgLoading.dismiss();
        if (isError)
            Log.e(TAG, message);
        else
            Log.d(TAG, message);
        Log.i(TAG, "READY");
    }

    public void btnShodanTest_OnClick(View view) {
        startActivity(new Intent(this, ActivityShodanTest.class));
    }
}
