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

    public static  String appDataDirectory;
    public static Map<Utils.Executable, String> executableFileLocations;
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

        // Copy the zip file from the raw resources to local storage
        String zipLoc = copyZipFileToLocalStorage();

        // Unzip the main fip file
        unzipMainNmapFile(zipLoc);

        Log.i(TAG, "READY");
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

    private void unzipMainNmapFile(String zipLocation) {
        // Once here the zip file should be copied
        // Go ahead and upzip it
        File file = new File(binaryFileLocation);
        Log.d(TAG, "Checking if zip file has been extracted...");
        if (!file.exists()) {
            Log.d(TAG, "Files have not been extracted. Let's do that now...");
            try {
                Log.d(TAG, "Unzipping zip file to local storage...");
                Utils.unzipFile(new File(zipLocation), new File(appDataDirectory));
                Log.d(TAG, "File unzipped successfully!");
            } catch (IOException ex) {
                Log.e(TAG, "IOException --> Unzipping File --> " + ex.getMessage());
            } catch (Exception ex) {
                Log.e(TAG, "Exception --> Unzipping File --> " + ex.getMessage());
            }

            // Mark all files with read/write permissions using a recursive method
            try {
                Log.d(TAG, "Marking files with read/write permissions...");
                // Mark all files with read/write permissions for everyone
                markFilePermissionsRW(file);
                Log.d(TAG, "Success!");
            } catch (Exception ex) {
                Log.d(TAG, "XXXXX ERROR --> " + ex.getMessage());
            }
            Log.d(TAG, "Done with read/write permissions!");

            // Mark executable files as executable
            Log.d(TAG, "Now that that's done, let's make the executable files...well...executable");
            int count = 0;
            for (Map.Entry<Utils.Executable, String> item : executableFileLocations.entrySet()) {
                count++;
                try {
                    Log.d(TAG, "Marking binary " + count + " of " + executableFileLocations.size() + " as executable...");
                    Process proc = Runtime.getRuntime().exec("chmod 777 " + item.getValue());
                    proc.waitFor();
                    Log.d(TAG, "Binary executable!");
                } catch (Exception ex) {
                    Log.d(TAG, "Error marking file as executable: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        } else {
            Log.d(TAG, "Looks like the zip file has been extracted already...");
        }
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

    private void markFilePermissionsRW(File fileLocation) throws IOException {
        if (!fileLocation.exists())
            throw new IOException("File/Directory does not exist!");

        if (fileLocation.isDirectory()) {
            File[] files = fileLocation.listFiles();
            Log.d(TAG, "Setting read/write permissions for " + String.valueOf(files.length) + " files in '" + fileLocation.getAbsolutePath() + "'");
            try {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        markFilePermissionsRW(files[i]);
                    } else {
                        Log.v(TAG, "----- Setting read/write permissions for file '" + files[i].getAbsolutePath() + "'");
                        Process proc  = Runtime.getRuntime().exec("chmod 666 " + files[i].getAbsolutePath());
                        proc.waitFor();
                        Log.v(TAG, "----- Success!");
                    }
                }
            } catch (Exception ex) {
                Log.v(TAG, "XXXXX ERROR --> " + ex.getMessage());
            }
        } else {
            try {
                Log.v(TAG, "----- Setting read/write permissions for file '" + fileLocation.getAbsolutePath() + "'");
                Process proc  = Runtime.getRuntime().exec("chmod 666 " + fileLocation.getAbsolutePath());
                proc.waitFor();
                Log.v(TAG, "----- Success!");
            } catch (Exception ex) {
                Log.v(TAG, "XXXXX ERROR --> " + ex.getMessage());
            }
        }
    }

    public void btnScanNetwork_OnClick(View view) {
        startActivity(new Intent(this, ActivityScan.class));
    }

    public void btnTerminal_OnClick(View view) {
        startActivity(new Intent(this, ActivityTerminal.class));
    }

    public void btnSimpleScan_OnClick(View view) {
        startActivity(new Intent(this, ActivityNmapMain.class));
    }
}
