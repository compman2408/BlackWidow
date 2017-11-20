package com.example.blackwidow;

import android.app.Activity;
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

public class ActivityMain extends Activity {
    private static final String TAG = "ActivityMain";

    private String _appDataDirectory;
    public static Map<Integer, String> binaryFilesToCopy;
    public static String binaryFileLocation;

    Button btnScanNetwork;
    Button btnSimpleScan;
    AnimationDrawable btnAnimation;

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
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());

        btnSimpleScan = (Button) findViewById(R.id.btnSimpleScan);
        // Start the scan button animation
        btnScanNetwork = (Button) findViewById(R.id.btnScanNetwork);
//        btnAnimation = (AnimationDrawable) btnScanNetwork.getBackground();

        btnAnimation = new AnimationScan(this, R.drawable.btn_scan_frame0);
        btnScanNetwork.setBackground(btnAnimation);

        // Set the directories to the important stuff
        _appDataDirectory = getApplicationContext().getFilesDir().getPath();
        binaryFileLocation = _appDataDirectory + "/bin";
        // Add files to copy
        binaryFilesToCopy = new HashMap<Integer, String>();
        binaryFilesToCopy.put(R.raw.ncat64, binaryFileLocation + "/ncat");
        binaryFilesToCopy.put(R.raw.ndiff64, binaryFileLocation + "/ndiff");
        binaryFilesToCopy.put(R.raw.nmap64, binaryFileLocation + "/nmap");
        binaryFilesToCopy.put(R.raw.nping64, binaryFileLocation + "/nping");
        binaryFilesToCopy.put(R.raw.uninstall_ndiff64, binaryFileLocation + "/uninstall_ndiff");

        binaryFilesToCopy.put(R.raw.nmap_mac_prefixes, binaryFileLocation + "/nmap-mac-prefixes");
        binaryFilesToCopy.put(R.raw.nmap_os_db, binaryFileLocation + "/nmap-os-db");
        binaryFilesToCopy.put(R.raw.nmap_payloads, binaryFileLocation + "/nmap-payloads");
        binaryFilesToCopy.put(R.raw.nmap_service_probes, binaryFileLocation + "/nmap-service-probes");
        binaryFilesToCopy.put(R.raw.nmap_services, binaryFileLocation + "/nmap-services");
        binaryFilesToCopy.put(R.raw.nmap_protocols, binaryFileLocation + "/nmap-protocols");
        binaryFilesToCopy.put(R.raw.nmap_rpc, binaryFileLocation + "/nmap-rpc");

        // Copy the files out of the raw resources so they can be executed
        copyAllBinaryFiles();
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

    private void copyAllBinaryFiles() {
        // If the binary files don't currently exist copy them out of the raw resources onto the
        // internal storage of the device since the external storage won't allow us to run the
        // executable on the Android system
        Log.wtf(TAG, "Checking binary directory existance...");
        File file = new File(binaryFileLocation);
        if (!file.exists()) {
            Log.wtf(TAG, "Binary directory doesn't exist. Creating directories...");
            // Create binary directory
            file.mkdirs();
            Log.wtf(TAG, "Directories created. Copying files...");
        } else {
            Log.wtf(TAG, "Binary directory already exists. Moving on to files.");
        }
        Log.wtf(TAG, "Checking binary files existance...");
        int count = 0;
        for (Map.Entry<Integer, String> item : binaryFilesToCopy.entrySet()) {
            count++;
            file = new File(item.getValue());
            if (!file.exists()) {
                Log.wtf(TAG, "Binary " + count + " out of " + binaryFilesToCopy.size() + " doesn't exist.");
                try {
                    Log.wtf(TAG, "Copying binary " + count + " out of " + binaryFilesToCopy.size());
                    copyBinFilesToInternalStorage(item.getKey(), item.getValue());
                    Log.wtf(TAG, "File copy successful!");
                } catch (IOException ex) {
                    Log.wtf(TAG, "Error copying file: " + ex.getMessage());
                    ex.printStackTrace();
                }
                try {
                    Log.wtf(TAG, "Making binary executable...");
                    Process process = Runtime.getRuntime().exec("chmod 777 " + item.getValue());
                    process.waitFor();
                    Log.wtf(TAG, "Binary executable!");
                } catch (Exception ex) {
                    Log.wtf(TAG, "Error marking file as executable: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                Log.wtf(TAG, "Binary " + count + " out of " + binaryFilesToCopy.size() + " already exists. Skipping file copy...");
            }
        }
    }

    private void copyBinFilesToInternalStorage(int id, String path) throws IOException {
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
        startActivity(new Intent(this, ActivityScan.class));
    }

    public void btnNmapTest_OnClick(View view) {
        startActivity(new Intent(this, ActivityNmapTest.class));
    }

    public void btnSimpleScan_OnClick(View view) {
        startActivity(new Intent(this, ActivityNmapMain.class));
    }
}
